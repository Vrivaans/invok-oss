import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray, FormsModule } from '@angular/forms';
import { ApiService } from '../api.service';
import { Router, RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-tools',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, FormsModule, TranslateModule],
  templateUrl: './tools.html',
  styleUrl: './tools.scss'
})
export class ToolsComponent implements OnInit {
  batchForm: FormGroup;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  openApiJsonText = '';
  importError = '';
  importSuccessMessage = '';
  hasOpenApiImportPending = false;

  providers: any[] = [];

  // Edit Provider Modal State
  showEditProviderModal = false;
  providerToEdit: any | null = null;
  editProviderForm: FormGroup;
  isEditingProvider = false;
  editProviderError = '';

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private translate: TranslateService
  ) {
    this.batchForm = this.fb.group({
      providerId: [''],
      isCreatingProvider: [true],
      providerName: [''],
      providerCode: [''], // Optional
      baseUrl: [''],
      authenticationType: ['NONE'],
      apiKeyLocation: ['HEADER'],
      apiKeyName: [''],
      apiKeyValue: [''],

      // Dynamic Auth Fields
      isDynamicAuth: [false],
      dynamicAuthUrl: [''],
      dynamicAuthMethod: ['POST'],
      dynamicAuthPayloadType: ['JSON'],
      dynamicAuthPayloadLocation: ['BODY'],
      dynamicAuthPayload: this.fb.array([]),
      dynamicAuthTokenExtractionPath: [''],
      dynamicAuthInvalidationKeywords: ['invalid_token,token_expired,unauthorized,expired_token'],

      // OAuth2 Fields
      oauth2ClientId: [''],
      oauth2ClientSecret: [''],
      oauth2AuthorizationUrl: [''],
      oauth2TokenUrl: [''],
      oauth2Scopes: [''],
      oauth2RedirectUri: [''],

      customHeaders: this.fb.array([]),
      // Secondary Auth Fields
      secondaryApiKeyName: [''],
      secondaryApiKeyValue: [''],
      secondaryApiKeyLocation: ['QUERY_PARAMETER'],
      // Array of individual tool endpoints
      endpoints: this.fb.array([])
    });

    this.editProviderForm = this.fb.group({
      name: ['', Validators.required],
      code: [''],
      baseUrl: ['', Validators.required],
      authenticationType: ['NONE'],
      apiKeyLocation: ['HEADER'],
      apiKeyName: [''],
      apiKeyValue: [''],

      // Dynamic Auth Fields
      isDynamicAuth: [false],
      dynamicAuthUrl: [''],
      dynamicAuthMethod: ['POST'],
      dynamicAuthPayloadType: ['JSON'],
      dynamicAuthPayloadLocation: ['BODY'],
      dynamicAuthPayload: this.fb.array([]),
      dynamicAuthTokenExtractionPath: [''],
      dynamicAuthInvalidationKeywords: ['invalid_token,token_expired,unauthorized,expired_token'],

      // OAuth2 Fields
      oauth2ClientId: [''],
      oauth2ClientSecret: [''],
      oauth2AuthorizationUrl: [''],
      oauth2TokenUrl: [''],
      oauth2Scopes: [''],
      oauth2RedirectUri: [''],

      secondaryApiKeyName: [''],
      secondaryApiKeyValue: [''],
      secondaryApiKeyLocation: ['QUERY_PARAMETER'],

      customHeaders: this.fb.array([])
    });
  }

  ngOnInit(): void {
    // Load existing providers
    this.apiService.getApiProviders().subscribe({
      next: (data) => {
        this.providers = data;
      },
      error: (error) => {
        console.error('Error loading API providers', error);
      }
    });
  }

  get endpoints() {
    return this.batchForm.get('endpoints') as FormArray;
  }

  getParameters(endpointIndex: number) {
    return this.endpoints.at(endpointIndex).get('parameters') as FormArray;
  }

  addEndpoint(data?: any) {
    const endpointForm = this.fb.group({
      name: [data?.name || '', Validators.required],
      code: [data?.code || ''],
      description: [data?.description || '', Validators.required],
      endpointPath: [data?.endpointPath || '', Validators.required],
      httpMethod: [data?.httpMethod || 'GET', Validators.required],
      enabled: [data?.enabled !== undefined ? data.enabled : true],
      parameters: this.fb.array([])
    });

    if (data && data.parameters) {
      const paramArray = endpointForm.get('parameters') as FormArray;
      data.parameters.forEach((p: any) => {
        paramArray.push(this.fb.group({
          name: [p.name, Validators.required],
          type: [p.type, Validators.required],
          description: [p.description || '', Validators.required],
          required: [p.required !== undefined ? p.required : true],
          defaultValue: [p.defaultValue || '']
        }));
      });
    }

    this.endpoints.push(endpointForm);
  }

  removeEndpoint(index: number) {
    this.endpoints.removeAt(index);
  }

  addParameter(endpointIndex: number) {
    const parameterForm = this.fb.group({
      name: ['', Validators.required],
      type: ['STRING', Validators.required],
      description: ['', Validators.required],
      required: [true],
      defaultValue: ['']
    });
    this.getParameters(endpointIndex).push(parameterForm);
  }

  removeParameter(endpointIndex: number, paramIndex: number) {
    this.getParameters(endpointIndex).removeAt(paramIndex);
  }

  get customHeaders() {
    return this.batchForm.get('customHeaders') as FormArray;
  }

  addCustomHeader() {
    this.customHeaders.push(this.fb.group({
      key: ['', Validators.required],
      value: ['', Validators.required]
    }));
  }

  removeCustomHeader(index: number) {
    this.customHeaders.removeAt(index);
  }

  get editCustomHeaders() {
    return this.editProviderForm.get('customHeaders') as FormArray;
  }

  addEditCustomHeader() {
    this.editCustomHeaders.push(this.fb.group({
      key: ['', Validators.required],
      value: ['', Validators.required]
    }));
  }

  removeEditCustomHeader(index: number) {
    this.editCustomHeaders.removeAt(index);
  }

  get dynamicAuthPayload() {
    return this.batchForm.get('dynamicAuthPayload') as FormArray;
  }

  addDynamicAuthPayload() {
    this.dynamicAuthPayload.push(this.fb.group({
      key: ['', Validators.required],
      value: ['', Validators.required]
    }));
  }

  removeDynamicAuthPayload(index: number) {
    this.dynamicAuthPayload.removeAt(index);
  }

  get editDynamicAuthPayload() {
    return this.editProviderForm.get('dynamicAuthPayload') as FormArray;
  }

  addEditDynamicAuthPayload() {
    this.editDynamicAuthPayload.push(this.fb.group({
      key: ['', Validators.required],
      value: ['', Validators.required]
    }));
  }

  removeEditDynamicAuthPayload(index: number) {
    this.editDynamicAuthPayload.removeAt(index);
  }

  // --- Provider Edit Logic ---
  openEditProviderModal() {
    const selectedId = this.batchForm.get('providerId')?.value;
    if (!selectedId) return;

    const provider = this.providers.find(p => p.id == selectedId);
    if (!provider) return;

    this.providerToEdit = provider;
    this.editProviderForm.patchValue({
      name: provider.name,
      code: provider.code,
      baseUrl: provider.baseUrl,
      authenticationType: provider.authenticationType,
      apiKeyLocation: provider.apiKeyLocation,
      apiKeyName: provider.apiKeyName,
      apiKeyValue: '',
      isDynamicAuth: provider.isDynamicAuth || false,
      dynamicAuthUrl: provider.dynamicAuthUrl || '',
      dynamicAuthMethod: provider.dynamicAuthMethod || 'POST',
      dynamicAuthPayloadType: provider.dynamicAuthPayloadType || 'JSON',
      dynamicAuthPayloadLocation: provider.dynamicAuthPayloadLocation || 'BODY',
      dynamicAuthTokenExtractionPath: provider.dynamicAuthTokenExtractionPath || '',
      dynamicAuthInvalidationKeywords: provider.dynamicAuthInvalidationKeywords || 'invalid_token,token_expired,unauthorized,expired_token',
      oauth2ClientId: provider.oauth2ClientId || '',
      oauth2ClientSecret: '',
      oauth2AuthorizationUrl: provider.oauth2AuthorizationUrl || '',
      oauth2TokenUrl: provider.oauth2TokenUrl || '',
      oauth2Scopes: provider.oauth2Scopes || '',
      oauth2RedirectUri: provider.oauth2RedirectUri || '',
      secondaryApiKeyName: provider.secondaryApiKeyName || '',
      secondaryApiKeyValue: '',
      secondaryApiKeyLocation: provider.secondaryApiKeyLocation || 'QUERY_PARAMETER'
    });

    this.editCustomHeaders.clear();
    if (provider.customHeaders) {
      Object.keys(provider.customHeaders).forEach(k => {
        this.editCustomHeaders.push(this.fb.group({
          key: [k, Validators.required],
          value: [provider.customHeaders[k], Validators.required]
        }));
      });
    }

    this.editDynamicAuthPayload.clear();
    if (provider.dynamicAuthPayload) {
      try {
        const payloadObj = JSON.parse(provider.dynamicAuthPayload);
        Object.keys(payloadObj).forEach(k => {
          this.editDynamicAuthPayload.push(this.fb.group({
            key: [k, Validators.required],
            value: [payloadObj[k], Validators.required]
          }));
        });
      } catch (e) {
        console.error('Error parsing dynamic payload', e);
      }
    }

    this.showEditProviderModal = true;
  }

  closeEditProviderModal() {
    this.showEditProviderModal = false;
    this.providerToEdit = null;
    this.editProviderForm.reset();
    this.editProviderError = '';
    this.isEditingProvider = false;
  }

  saveProviderChanges() {
    if (this.editProviderForm.invalid || !this.providerToEdit) {
      this.editProviderForm.markAllAsTouched();
      return;
    }

    this.isEditingProvider = true;
    this.editProviderError = '';

    let dynamicPayloadStr = '';
    if (this.editProviderForm.value.isDynamicAuth) {
      const payloadObj = this.editDynamicAuthPayload.value.reduce((acc: any, curr: any) => {
        if (curr.key && curr.value) acc[curr.key] = curr.value;
        return acc;
      }, {});
      if (Object.keys(payloadObj).length > 0) {
        dynamicPayloadStr = JSON.stringify(payloadObj);
      }
    }

    const payload = {
      ...this.editProviderForm.value,
      dynamicAuthPayload: dynamicPayloadStr,
      customHeaders: this.editCustomHeaders.value.reduce((acc: any, curr: any) => {
        if (curr.key && curr.value) acc[curr.key] = curr.value;
        return acc;
      }, {}),
      secondaryApiKeyName: this.editProviderForm.value.secondaryApiKeyName,
      secondaryApiKeyValue: this.editProviderForm.value.secondaryApiKeyValue,
      secondaryApiKeyLocation: this.editProviderForm.value.secondaryApiKeyLocation,
    };

    this.apiService.updateApiProvider(this.providerToEdit.id, payload).subscribe({
      next: (updatedProvider) => {
        this.isEditingProvider = false;
        this.ngOnInit(); // Refresh provider list
        this.closeEditProviderModal();
      },
      error: (err) => {
        this.isEditingProvider = false;
        this.editProviderError = err.error?.message || 'Error al actualizar el proveedor.';
        console.error('Error updating provider', err);
      }
    });
  }

  importOpenApi() {
    this.importError = '';
    this.importSuccessMessage = '';
    this.hasOpenApiImportPending = false;

    if (!this.openApiJsonText.trim()) {
      this.importError = 'Pega un JSON válido primero.';
      return;
    }

    try {
      const spec = JSON.parse(this.openApiJsonText);

      // --- NEW FEATURE: Dual Detection (Invok Provider Export vs OpenAPI) ---
      if (Array.isArray(spec)) {
        // It's a Invok JSON Export, import directly
        this.isSubmitting = true;
        this.apiService.importProviders(spec).subscribe({
          next: () => {
            this.isSubmitting = false;
            this.importSuccessMessage = this.translate.instant('TOOLS.IMPORT_SUCCESS_INVOK');
            this.openApiJsonText = '';
            setTimeout(() => this.router.navigate(['/home']), 1500);
          },
          error: (err) => {
            this.isSubmitting = false;
            this.importError = err.error?.message || this.translate.instant('TOOLS.IMPORT_ERROR_INVOK');
          }
        });
        return; // Wait for the async call to complete
      }
      // ------------------------------------------------------------------------

      // Try extracting base URL if present
      if (spec.servers?.length > 0) {
        const serverUrl = spec.servers[0].url;
        if (this.isAbsoluteUrl(serverUrl)) {
          this.batchForm.patchValue({ baseUrl: serverUrl });
        }
      }

      if (spec.info?.title) {
        this.batchForm.patchValue({ providerName: spec.info.title });
        this.batchForm.patchValue({ providerCode: spec.info.title.replaceAll(/[^a-zA-Z0-9-]/g, '-').toLowerCase() });
      } else {
        this.batchForm.patchValue({ providerName: 'Imported API - ' + new Date().toLocaleDateString() });
      }

      this.batchForm.patchValue({ isCreatingProvider: true });

      if (!spec.paths) {
        this.importError = 'No se encontraron "paths" en el JSON.';
        return;
      }

      let importCount = 0;

      // Clear existing if any
      while (this.endpoints.length !== 0) {
        this.endpoints.removeAt(0);
      }

      Object.keys(spec.paths).forEach(path => {
        const pathItem = spec.paths[path];

        ['get', 'post', 'put', 'delete'].forEach(method => {
          if (pathItem[method]) {
            const operation = pathItem[method];

            // Map OpenAPI params to our model
            const parsedParams: any[] = [];
            if (operation.parameters) {
              operation.parameters.forEach((param: any) => {
                let localType = 'STRING';
                const schemaType = param.schema?.type;
                if (schemaType) {
                  if (schemaType === 'integer' || schemaType === 'number') localType = 'NUMBER';
                  if (schemaType === 'boolean') localType = 'BOOLEAN';
                }
                parsedParams.push({
                  name: param.name,
                  type: localType,
                  description: param.description || `Parameter ${param.name}`,
                  required: !!param.required
                });
              });
            }

            // Also look for requestBody for POST/PUT if needed (simplified representation as a param)
            if (operation.requestBody) {
              parsedParams.push({
                name: 'body',
                type: 'STRING',
                description: 'Request Body (JSON string)',
                required: true
              });
            }
            // Generate code safely
            const generatedCode = (operation.operationId)
              ? operation.operationId.replaceAll(/[^a-zA-Z0-9-]/g, '-').toLowerCase()
              : `${method}-${path.replaceAll(/[^a-zA-Z0-9]/g, '-')}`.toLowerCase();

            this.addEndpoint({
              name: operation.summary || operation.operationId || `${method.toUpperCase()} ${path}`,
              code: generatedCode,
              description: operation.description || operation.summary || `Llama al endpoint ${path}`,
              endpointPath: path,
              httpMethod: method.toUpperCase(),
              parameters: parsedParams
            });
            importCount++;
          }
        });
      });

      this.hasOpenApiImportPending = importCount > 0;
      this.importSuccessMessage = this.translate.instant('TOOLS.IMPORT_SUCCESS_REVIEW', { count: importCount });
      if (this.hasOpenApiImportPending && !this.batchForm.get('baseUrl')?.value) {
        this.importSuccessMessage += ' ' + this.translate.instant('TOOLS.IMPORT_BASE_URL_REQUIRED');
      }
      this.openApiJsonText = ''; // Clear text area

    } catch (e: any) {
      this.importError = 'JSON inválido o estructura no reconocida: ' + e.message;
    }
  }

  refreshCache() {
    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';
    this.apiService.refreshToolCache().subscribe({
      next: (res) => {
        this.isSubmitting = false;
        this.successMessage = res.message || 'Caché de herramientas actualizada correctamente';
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Error al refrescar la caché.';
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  onSubmit() {
    if (this.batchForm.invalid || this.endpoints.length === 0) {
      this.batchForm.markAllAsTouched();
      this.errorMessage = 'Por favor, revise que todos los campos requeridos estén completos y que haya al menos un endpoint.';
      return;
    }

    this.isSubmitting = true;
    this.successMessage = '';
    this.errorMessage = '';

    const formValue = this.batchForm.value;

    if (formValue.isCreatingProvider) {
      if (!formValue.providerName || !formValue.baseUrl) {
        this.errorMessage = this.getMissingNewProviderMessage();
        this.isSubmitting = false;
        return;
      }

      const providerPayload = {
        name: formValue.providerName,
        code: formValue.providerCode,
        baseUrl: formValue.baseUrl,
        authenticationType: formValue.authenticationType,
        apiKeyLocation: formValue.apiKeyLocation,
        apiKeyName: formValue.apiKeyName,
        apiKeyValue: formValue.apiKeyValue,
        isDynamicAuth: formValue.isDynamicAuth,
        dynamicAuthUrl: formValue.dynamicAuthUrl,
        dynamicAuthMethod: formValue.dynamicAuthMethod,
        dynamicAuthPayloadType: formValue.dynamicAuthPayloadType,
        dynamicAuthPayloadLocation: formValue.dynamicAuthPayloadLocation,
        dynamicAuthPayload: (() => {
          if (!formValue.isDynamicAuth) return '';
          const pObj = formValue.dynamicAuthPayload.reduce((acc: any, curr: any) => {
            if (curr.key && curr.value) acc[curr.key] = curr.value;
            return acc;
          }, {});
          return Object.keys(pObj).length > 0 ? JSON.stringify(pObj) : '';
        })(),
        dynamicAuthTokenExtractionPath: formValue.dynamicAuthTokenExtractionPath,
        dynamicAuthInvalidationKeywords: formValue.dynamicAuthInvalidationKeywords,
        // OAuth2
        isOauth2: formValue.authenticationType === 'OAUTH2_AUTHORIZATION_CODE',
        oauth2ClientId: formValue.oauth2ClientId,
        oauth2ClientSecret: formValue.oauth2ClientSecret,
        oauth2AuthorizationUrl: formValue.oauth2AuthorizationUrl,
        oauth2TokenUrl: formValue.oauth2TokenUrl,
        oauth2Scopes: formValue.oauth2Scopes,
        // Redirect URI is always derived from the provider code (no manual input needed)
        oauth2RedirectUri: formValue.authenticationType === 'OAUTH2_AUTHORIZATION_CODE' && formValue.providerCode
          ? `${window.location.origin}/api/oauth2/callback/${formValue.providerCode}`
          : '',
        customHeaders: formValue.customHeaders.reduce((acc: any, curr: any) => {
          if (curr.key && curr.value) acc[curr.key] = curr.value;
          return acc;
        }, {}),
        secondaryApiKeyName: formValue.secondaryApiKeyName,
        secondaryApiKeyValue: formValue.secondaryApiKeyValue,
        secondaryApiKeyLocation: formValue.secondaryApiKeyLocation
      };

      this.apiService.createApiProvider(providerPayload).subscribe({
        next: (providerResponse) => {
          this.executeBatchCreation(providerResponse.id, formValue.endpoints, true);
        },
        error: (error) => {
          this.isSubmitting = false;
          if (error.error?.message) {
            this.errorMessage = error.error.message;
          } else {
            this.errorMessage = 'Hubo un error al crear el proveedor base.';
          }
          console.error('Error creating provider', error);
        }
      });
    } else {
      if (!formValue.providerId) {
        this.errorMessage = this.getMissingExistingProviderMessage();
        this.isSubmitting = false;
        return;
      }
      this.executeBatchCreation(formValue.providerId, formValue.endpoints, false);
    }
  }

  private executeBatchCreation(providerId: number | string, endpointsData: any[], isNewProvider: boolean) {
    const apiToolsToCreate: any[] = endpointsData.map((ep: any) => ({
      ...ep,
      providerId: Number(providerId)
    }));

    this.apiService.createApiToolsBatch(apiToolsToCreate).subscribe({
      next: () => {
        this.isSubmitting = false;
        if (isNewProvider) {
          this.successMessage = this.translate.instant('TOOLS.BATCH_SUCCESS_NEW', { count: apiToolsToCreate.length });
        } else {
          this.successMessage = this.translate.instant('TOOLS.BATCH_SUCCESS_EXISTING', { count: apiToolsToCreate.length });
        }

        // Clear the form
        this.batchForm.reset({ authenticationType: 'NONE', apiKeyLocation: 'HEADER', isCreatingProvider: true, isDynamicAuth: false, dynamicAuthMethod: 'POST', dynamicAuthPayloadType: 'JSON', dynamicAuthPayloadLocation: 'BODY', dynamicAuthInvalidationKeywords: 'invalid_token,token_expired,unauthorized,expired_token' });
        this.customHeaders.clear();
        this.dynamicAuthPayload.clear();
        while (this.endpoints.length !== 0) {
          this.endpoints.removeAt(0);
        }
        this.hasOpenApiImportPending = false;
        setTimeout(() => this.router.navigate(['/home']), 1500);
      },
      error: (error) => {
        this.isSubmitting = false;
        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = 'Hubo un error al guardar el lote de herramientas.';
        }
        console.error('Error batch creating tools', error);
      }
    });
  }

  private isAbsoluteUrl(url: string | undefined): boolean {
    return !!url && /^https?:\/\//i.test(url);
  }

  private getMissingNewProviderMessage(): string {
    return this.hasOpenApiImportPending
      ? this.translate.instant('TOOLS.OPENAPI_PROVIDER_REQUIRED')
      : 'Debe indicar el nombre y la URL base del nuevo proveedor.';
  }

  private getMissingExistingProviderMessage(): string {
    return this.hasOpenApiImportPending
      ? this.translate.instant('TOOLS.OPENAPI_EXISTING_PROVIDER_REQUIRED')
      : 'Debe seleccionar un proveedor existente o crear uno nuevo.';
  }
}
