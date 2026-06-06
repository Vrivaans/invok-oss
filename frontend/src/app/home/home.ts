import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray, FormsModule } from '@angular/forms';
import { ApiService, ApiTool } from '../api.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';


@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule, TranslateModule],
    templateUrl: './home.html',
    styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {
    private apiService = inject(ApiService);
    private fb = inject(FormBuilder);
    private translate = inject(TranslateService);

    storedTools: ApiTool[] = [];
    groupedStoredTools: { providerName: string, providerId?: number, tools: ApiTool[] }[] = [];
    // Active tools are tools with enabled=true — derived client-side, no extra request needed
    get activeTools(): ApiTool[] {
        return this.storedTools.filter(t => t.enabled);
    }

    error: string | null = null;

    // Delete Modal State
    showDeleteModal = false;
    toolToDelete: number | null = null;
    toolToDeleteName: string = '';

    // Delete Provider Modal State
    showDeleteProviderModal = false;
    providerToDelete: number | null = null;
    providerToDeleteName: string = '';

    // Edit Modal State
    showEditModal = false;
    toolToEdit: ApiTool | null = null;
    editForm: FormGroup;
    isSubmittingEdit = false;

    // Export Modal State
    showExportModal = false;
    exportableProviders: any[] = [];
    selectedToolCodes = new Set<string>();
    exportSearchQuery = '';
    isExporting = false;
    invokUrl = window.location.origin;
    isSubmittingRefresh = false;
    refreshSuccessMessage = '';
    refreshErrorMessage = '';

    // Welcome Onboarding
    showWelcomeModal = false;

    constructor() {
        this.editForm = this.fb.group({
            name: ['', Validators.required],
            code: [''],
            description: ['', Validators.required],
            endpointPath: ['', Validators.required],
            httpMethod: ['GET', Validators.required],
            bodyPayloadTemplate: [''],
            enabled: [true],
            healthy: [true],
            parameters: this.fb.array([])
        });
    }

    get parameters() {
        return this.editForm.get('parameters') as FormArray;
    }

    addParameter() {
        const parameterForm = this.fb.group({
            name: ['', Validators.required],
            type: ['STRING', Validators.required],
            description: ['', Validators.required],
            required: [true],
            defaultValue: ['']
        });
        this.parameters.push(parameterForm);
    }

    removeParameter(index: number) {
        this.parameters.removeAt(index);
    }

    ngOnInit() {
        this.loadData();
        this.checkFirstLogin();
    }

    checkFirstLogin() {
        const isFirstLogin = localStorage.getItem('firstLogin') === 'true';
        if (isFirstLogin) {
            this.showWelcomeModal = true;
        }
    }

    closeWelcomeModal() {
        this.showWelcomeModal = false;
        localStorage.removeItem('firstLogin');
    }

    loadData() {
        this.apiService.getStoredTools().subscribe({
            next: (tools) => {
                this.storedTools = tools;
                this.updateGroupedTools();
            },
            error: (err) => console.error('Error fetching stored tools', err)
        });
    }

    private updateGroupedTools() {
        const map = new Map<string, { providerName: string, providerId?: number, tools: ApiTool[] }>();
        this.storedTools.forEach(tool => {
            const pName = tool.providerName || 'Proveedores Nativos/Sin Especificar';
            if (!map.has(pName)) {
                map.set(pName, { providerName: pName, providerId: tool.providerId, tools: [] });
            }
            map.get(pName)!.tools.push(tool);
        });
        this.groupedStoredTools = Array.from(map.values());
    }


    deleteTool(id: number, name: string, event: Event) {
        event.preventDefault();
        event.stopPropagation();
        this.toolToDelete = id;
        this.toolToDeleteName = name;
        this.showDeleteModal = true;
    }

    closeDeleteModal() {
        this.showDeleteModal = false;
        this.toolToDelete = null;
        this.toolToDeleteName = '';
    }

    confirmDelete() {
        if (this.toolToDelete !== null) {
            this.apiService.deleteApiTool(this.toolToDelete).subscribe({
                next: () => {
                    this.loadData();
                    this.closeDeleteModal();
                },
                error: (err) => console.error('Error deleting tool', err)
            });
        }
    }

    deleteProvider(id: number | undefined, name: string, event: Event) {
        event.preventDefault();
        event.stopPropagation();
        if (id === undefined) return;
        this.providerToDelete = id;
        this.providerToDeleteName = name;
        this.showDeleteProviderModal = true;
    }

    closeDeleteProviderModal() {
        this.showDeleteProviderModal = false;
        this.providerToDelete = null;
        this.providerToDeleteName = '';
    }

    confirmDeleteProvider() {
        if (this.providerToDelete !== null) {
            this.apiService.deleteApiProvider(this.providerToDelete).subscribe({
                next: () => {
                    this.loadData();
                    this.closeDeleteProviderModal();
                },
                error: (err) => console.error('Error deleting provider', err)
            });
        }
    }

    // Edit Logic
    openEditModal(tool: ApiTool) {
        this.toolToEdit = tool;

        this.parameters.clear();
        if (tool.parameters && tool.parameters.length > 0) {
            tool.parameters.forEach(p => {
                this.parameters.push(this.fb.group({
                    id: [p.id],
                    name: [p.name, Validators.required],
                    type: [p.type || 'STRING', Validators.required],
                    description: [p.description || '', Validators.required],
                    required: [p.required !== undefined ? p.required : true],
                    defaultValue: [p.defaultValue || '']
                }));
            });
        }

        this.editForm.patchValue({
            name: tool.name,
            code: tool.code,
            description: tool.description,
            endpointPath: tool.endpointPath,
            httpMethod: tool.httpMethod,
            bodyPayloadTemplate: tool.bodyPayloadTemplate || '',
            enabled: tool.enabled,
            healthy: tool.healthy !== undefined ? tool.healthy : true
        });
        this.showEditModal = true;
    }

    closeEditModal() {
        this.showEditModal = false;
        this.toolToEdit = null;
        this.editForm.reset();
        this.error = null;
    }

    saveEditChanges() {
        if (this.editForm.invalid || !this.toolToEdit || !this.toolToEdit.id) {
            this.editForm.markAllAsTouched();
            return;
        }

        this.isSubmittingEdit = true;
        this.error = null;

        const updatedData = {
            ...this.toolToEdit, // Preserve provider configs, api keys etc
            ...this.editForm.value
        };

        this.apiService.updateApiTool(this.toolToEdit.id, updatedData).subscribe({
            next: () => {
                this.isSubmittingEdit = false;
                this.loadData();
                this.closeEditModal();
            },
            error: (err) => {
                this.isSubmittingEdit = false;
                if (err.error && err.error.message) {
                    this.error = err.error.message;
                } else {
                    this.error = this.translate.instant('ERRORS.UPDATE_TOOL');
                }
                console.error('Error updating tool', err);
            }
        });
    }

    // Export Logic
    openExportModal() {
        this.selectedToolCodes.clear();
        this.exportSearchQuery = '';
        this.apiService.getExportableProviders().subscribe({
            next: (data) => {
                this.exportableProviders = data;
                this.showExportModal = true;
            },
            error: (err) => {
                console.error('Error fetching exportable providers', err);
                alert('No se pudieron cargar los proveedores exportables.');
            }
        });
    }

    closeExportModal() {
        this.showExportModal = false;
        this.exportableProviders = [];
        this.selectedToolCodes.clear();
        this.exportSearchQuery = '';
    }

    get filteredExportableProviders(): any[] {
        if (!this.exportSearchQuery || !this.exportSearchQuery.trim()) {
            return this.exportableProviders;
        }
        const query = this.exportSearchQuery.toLowerCase().trim();
        
        return this.exportableProviders.map(provider => {
            const providerMatches = provider.name.toLowerCase().includes(query) || 
                                    (provider.baseUrl && provider.baseUrl.toLowerCase().includes(query));
                                    
            const matchedTools = provider.tools ? provider.tools.filter((tool: any) => 
                tool.name.toLowerCase().includes(query) || 
                tool.code.toLowerCase().includes(query) ||
                (tool.description && tool.description.toLowerCase().includes(query))
            ) : [];
            
            const toolsToShow = providerMatches ? (provider.tools || []) : matchedTools;
            
            if (providerMatches || matchedTools.length > 0) {
                return {
                    ...provider,
                    tools: toolsToShow
                };
            }
            return null;
        }).filter(p => p !== null);
    }

    toggleToolSelection(tool: any) {
        if (this.selectedToolCodes.has(tool.code)) {
            this.selectedToolCodes.delete(tool.code);
        } else {
            this.selectedToolCodes.add(tool.code);
        }
    }

    isProviderFullySelected(provider: any): boolean {
        if (!provider.tools || provider.tools.length === 0) return false;
        return provider.tools.every((t: any) => this.selectedToolCodes.has(t.code));
    }

    isProviderPartiallySelected(provider: any): boolean {
        if (!provider.tools || provider.tools.length === 0) return false;
        const selectedCount = provider.tools.filter((t: any) => this.selectedToolCodes.has(t.code)).length;
        return selectedCount > 0 && selectedCount < provider.tools.length;
    }

    toggleProviderSelection(provider: any) {
        const isFullySelected = this.isProviderFullySelected(provider);
        if (isFullySelected) {
            if (provider.tools) {
                provider.tools.forEach((t: any) => this.selectedToolCodes.delete(t.code));
            }
        } else {
            if (provider.tools) {
                provider.tools.forEach((t: any) => this.selectedToolCodes.add(t.code));
            }
        }
    }

    selectAllExport() {
        if (this.isAllToolsSelected()) {
            this.selectedToolCodes.clear();
        } else {
            this.exportableProviders.forEach(p => {
                if (p.tools) {
                    p.tools.forEach((t: any) => this.selectedToolCodes.add(t.code));
                }
            });
        }
    }

    isAllToolsSelected(): boolean {
        let totalToolsCount = 0;
        this.exportableProviders.forEach(p => {
            if (p.tools) totalToolsCount += p.tools.length;
        });
        return totalToolsCount > 0 && this.selectedToolCodes.size === totalToolsCount;
    }

    exportSelectedProviders() {
        this.isExporting = true;

        const providersToExport = this.exportableProviders.map(provider => {
            const tools = provider.tools ? provider.tools.filter((t: any) => this.selectedToolCodes.has(t.code)) : [];
            if (tools.length > 0) {
                return {
                    ...provider,
                    tools: tools
                };
            }
            return null;
        }).filter(p => p !== null);

        if (providersToExport.length === 0) {
            alert('Por favor, selecciona al menos una herramienta para exportar.');
            this.isExporting = false;
            return;
        }

        const dataStr = JSON.stringify(providersToExport, null, 2);
        const blob = new Blob([dataStr], { type: 'application/json;charset=utf-8' });
        const url = window.URL.createObjectURL(blob);
        const exportFileDefaultName = 'invok_herramientas_publicas.json';

        const linkElement = document.createElement('a');
        linkElement.setAttribute('href', url);
        linkElement.setAttribute('download', exportFileDefaultName);
        document.body.appendChild(linkElement);
        linkElement.click();
        document.body.removeChild(linkElement);
        window.URL.revokeObjectURL(url);

        this.isExporting = false;
        this.closeExportModal();
    }

    exportAsN8nWorkflowProxy() {
        this.isExporting = true;

        const providersToExport = this.exportableProviders.map(provider => {
            const tools = provider.tools ? provider.tools.filter((t: any) => this.selectedToolCodes.has(t.code)) : [];
            if (tools.length > 0) {
                return {
                    ...provider,
                    tools: tools
                };
            }
            return null;
        }).filter(p => p !== null);

        if (providersToExport.length === 0) {
            alert('Por favor, selecciona al menos una herramienta para exportar.');
            this.isExporting = false;
            return;
        }

        const nodes: any[] = [];
        const connections: any = {};
        
        let x = 250;
        let y = 300;
        let index = 1;

        let baseUrl = this.invokUrl ? this.invokUrl.trim() : 'http://localhost:8080';
        if (baseUrl.endsWith('/')) {
            baseUrl = baseUrl.slice(0, -1);
        }

        providersToExport.forEach(provider => {
            if (provider.tools) {
                provider.tools.forEach((tool: any) => {
                    const nodeName = `Invok: ${tool.code}`;
                    const nodeType = 'n8n-nodes-base.httpRequest';
                    
                    const bodyParams: any = {};
                    if (tool.parameters) {
                        tool.parameters.forEach((param: any) => {
                            bodyParams[param.name] = param.defaultValue !== undefined ? param.defaultValue : '';
                        });
                    }

                    const parameters: any = {
                        method: 'POST',
                        url: `${baseUrl}/api/v1/execute/${tool.code}`,
                        sendHeaders: true,
                        headerParameters: {
                            parameters: [
                                {
                                    name: 'X-HandsAI-Token',
                                    value: 'YOUR_PAT_TOKEN'
                                }
                            ]
                        },
                        sendBody: true,
                        specifyBody: 'json',
                        jsonBody: JSON.stringify(bodyParams, null, 2),
                        options: {}
                    };

                    nodes.push({
                        parameters: parameters,
                        id: `invok-node-${tool.code}-${index++}`,
                        name: nodeName,
                        type: nodeType,
                        typeVersion: 4.2,
                        position: [x, y]
                    });

                    x += 250;
                    if (x > 1500) {
                        x = 250;
                        y += 200;
                    }
                });
            }
        });

        const n8nWorkflow = {
            name: 'Workflow Invok (Proxy)',
            nodes: nodes,
            connections: connections,
            active: false,
            settings: {
                executionOrder: 'v1'
            },
            meta: {
                templateCredsSetupCompleted: true
            }
        };

        const dataStr = JSON.stringify(n8nWorkflow, null, 2);
        const blob = new Blob([dataStr], { type: 'application/json;charset=utf-8' });
        const url = window.URL.createObjectURL(blob);
        const exportFileDefaultName = 'invok_n8n_proxy_workflow.json';

        const linkElement = document.createElement('a');
        linkElement.setAttribute('href', url);
        linkElement.setAttribute('download', exportFileDefaultName);
        document.body.appendChild(linkElement);
        linkElement.click();
        document.body.removeChild(linkElement);
        window.URL.revokeObjectURL(url);

        this.isExporting = false;
        this.closeExportModal();
    }

    exportAsN8nWorkflowDirect() {
        this.isExporting = true;

        const providerIds = this.exportableProviders
            .filter(provider => provider.tools && provider.tools.some((t: any) => this.selectedToolCodes.has(t.code)))
            .map(provider => provider.id)
            .filter((id): id is number => id !== null && id !== undefined);

        if (providerIds.length === 0) {
            alert('Por favor, selecciona al menos una herramienta para exportar.');
            this.isExporting = false;
            return;
        }

        this.apiService.getN8nWorkflowExport(providerIds, 'Invok — Direct API Export').subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const linkElement = document.createElement('a');
                linkElement.setAttribute('href', url);
                linkElement.setAttribute('download', 'invok_n8n_direct_workflow.json');
                document.body.appendChild(linkElement);
                linkElement.click();
                document.body.removeChild(linkElement);
                window.URL.revokeObjectURL(url);
                this.isExporting = false;
                this.closeExportModal();
            },
            error: (err) => {
                console.error('Error downloading direct workflow', err);
                alert('Ocurrió un error al descargar el flujo directo de n8n.');
                this.isExporting = false;
            }
        });
    }

    refreshCache() {
        this.isSubmittingRefresh = true;
        this.refreshSuccessMessage = '';
        this.refreshErrorMessage = '';
        this.apiService.refreshToolCache().subscribe({
            next: (res) => {
                this.isSubmittingRefresh = false;
                const match = (res.message || '').match(/\((\d+)\s*tools\)/i);
                const count = match ? parseInt(match[1], 10) : 0;
                this.refreshSuccessMessage = this.translate.instant('TOOLS.REFRESH_CACHE_SUCCESS', { count });
                setTimeout(() => this.refreshSuccessMessage = '', 4000);
            },
            error: (err) => {
                this.isSubmittingRefresh = false;
                this.refreshErrorMessage = err.error?.message || 'Error al refrescar la caché.';
                setTimeout(() => this.refreshErrorMessage = '', 5000);
            }
        });
    }
}
