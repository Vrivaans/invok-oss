import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ApiTool {
    id?: number;
    name: string;
    code: string;
    description: string;
    providerId?: number;
    providerName?: string;
    baseUrl?: string;
    endpointPath: string;
    httpMethod: string;
    bodyPayloadTemplate?: string;
    authenticationType?: string;
    apiKeyLocation?: string;
    apiKeyName?: string;
    apiKeyValue?: string;
    enabled: boolean;
    healthy?: boolean;
    parameters?: any[];
}

export interface McpTool {
    name: string;
    description: string;
    inputSchema: any;
}

export interface McpListResponse {
    result: {
        tools: McpTool[];
    };
}

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private http = inject(HttpClient);

    getStoredTools(): Observable<ApiTool[]> {
        return this.http.get<ApiTool[]>('/api/tools');
    }

    getActiveTools(): Observable<McpListResponse> {
        return this.http.get<McpListResponse>('/mcp/tools/list');
    }

    createApiTool(tool: ApiTool): Observable<ApiTool> {
        return this.http.post<ApiTool>('/api/tools', tool);
    }

    updateApiTool(id: number, tool: ApiTool): Observable<ApiTool> {
        return this.http.put<ApiTool>(`/api/tools/${id}`, tool);
    }

    createApiToolsBatch(tools: ApiTool[]): Observable<any> {
        return this.http.post<any>('/api/tools/batch', tools);
    }

    createApiProvider(provider: any): Observable<any> {
        return this.http.post<any>('/api/providers', provider);
    }

    updateApiProvider(id: number, provider: any): Observable<any> {
        return this.http.put<any>(`/api/providers/${id}`, provider);
    }

    getApiProviders(): Observable<any[]> {
        return this.http.get<any[]>('/api/providers');
    }

    getExportableProviders(ids?: number[]): Observable<any[]> {
        let url = '/api/export';
        if (ids && ids.length > 0) {
            url += `?ids=${ids.join(',')}`;
        }
        return this.http.get<any[]>(url);
    }

    importProviders(payload: any[] | object): Observable<any> {
        return this.http.post<any>('/api/import', payload);
    }

    deleteApiTool(id: number): Observable<void> {
        return this.http.delete<void>(`/api/tools/${id}`);
    }

    deleteApiProvider(id: number): Observable<void> {
        return this.http.delete<void>(`/api/providers/${id}`);
    }
}
