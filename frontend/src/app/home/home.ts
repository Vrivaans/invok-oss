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
    selectedExportIds: Set<number> = new Set();
    isExporting = false;
    invokUrl = window.location.origin;
    handsAiToken = '';

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
        this.selectedExportIds.clear();
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
        this.selectedExportIds.clear();
    }

    toggleExportSelection(id: number) {
        if (this.selectedExportIds.has(id)) {
            this.selectedExportIds.delete(id);
        } else {
            this.selectedExportIds.add(id);
        }
    }

    selectAllExport() {
        if (this.selectedExportIds.size === this.exportableProviders.length) {
            this.selectedExportIds.clear();
        } else {
            this.exportableProviders.forEach((p, idx) => {
                this.selectedExportIds.add(idx); // Use index instead of name to match toggle logic
            });
        }
    }

    exportSelectedProviders() {
        this.isExporting = true;
        // Construct query URL
        // Actually, we need IDs to build the query.
        // Wait, does ExportApiProviderDto have ID? No! Look at ExportApiProviderDto.java: it only has name, baseUrl, etc.
        // If it doesn't have ID, how can we filter? 
        // Ah, the frontend needs to fetch ALL exportable providers, let user select by NAME, then just filter the JS array locally and download it!
        // That is much better and requires no extra API backend logic!

        let providersToExport = this.exportableProviders;
        if (this.selectedExportIds.size > 0) {
            providersToExport = this.exportableProviders.filter((_, index) => this.selectedExportIds.has(index));
        }

        const dataStr = JSON.stringify(providersToExport, null, 2);

        // Use Blob instead of data URI to force the browser to respect the file name
        const blob = new Blob([dataStr], { type: 'application/json;charset=utf-8' });
        const url = window.URL.createObjectURL(blob);

        const exportFileDefaultName = 'invok_herramientas_publicas.json';

        const linkElement = document.createElement('a');
        linkElement.setAttribute('href', url);
        linkElement.setAttribute('download', exportFileDefaultName);
        document.body.appendChild(linkElement); // Required for Firefox
        linkElement.click();
        document.body.removeChild(linkElement);
        window.URL.revokeObjectURL(url);

        this.isExporting = false;
        this.closeExportModal();
    }

    exportAsN8nWorkflow() {
        this.isExporting = true;

        let providersToExport = this.exportableProviders;
        if (this.selectedExportIds.size > 0) {
            providersToExport = this.exportableProviders.filter((_, index) => this.selectedExportIds.has(index));
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

                    const headerParametersList = [];
                    if (this.handsAiToken && this.handsAiToken.trim()) {
                        headerParametersList.push({
                            name: 'X-HandsAI-Token',
                            value: this.handsAiToken.trim()
                        });
                    } else {
                        headerParametersList.push({
                            name: 'X-HandsAI-Token',
                            value: 'YOUR_PAT_TOKEN'
                        });
                    }

                    const parameters: any = {
                        method: 'POST',
                        url: `${baseUrl}/api/v1/execute/${tool.code}`,
                        sendHeaders: true,
                        headerParameters: {
                            parameters: headerParametersList
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
            name: 'Workflow Invok',
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
        const exportFileDefaultName = 'invok_n8n_workflow.json';

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
}
