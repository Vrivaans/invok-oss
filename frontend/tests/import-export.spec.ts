import { test, expect } from '@playwright/test';

test.describe('Import & Export Flow', () => {

    test('should download exported providers explicitly', async ({ page }) => {
        // Mock the providers that can be exported
        await page.route('**/admin/providers', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([
                        { id: 7, name: 'Stripe API', isExportable: true },
                        { id: 8, name: 'Weather API', isExportable: true }
                    ])
                });
            } else {
                route.continue();
            }
        });

        // Mock home loads normally avoiding hangs
        await page.route('**/admin/tools/api', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({ status: 200, json: [] });
            } else {
                route.continue();
            }
        });
        await page.route('**/mcp/tools/list', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({ status: 200, json: { result: { tools: [] } } });
            } else {
                route.continue();
            }
        });

        await page.goto('/home');

        // Open Export Modal
        const exportHeaderBtn = page.locator('button.btn.primary', { hasText: '☁️ Exportar Herramientas Públicas' });
        await expect(exportHeaderBtn).toBeVisible();
        await exportHeaderBtn.click();

        // Verify Modal opens and rendering Stripe and Weather API
        const exportModal = page.locator('.modal-content', { hasText: 'Exportar Herramientas Públicas' });
        await expect(exportModal).toBeVisible();

        // Select All providers
        await page.locator('button', { hasText: 'Seleccionar Todos' }).click();

        // Expect true download interceptor
        const downloadPromise = page.waitForEvent('download');

        // Mock export endpoint API so we don't actually hit backend
        await page.route('**/api/export/providers?ids=7,8', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([{ name: "Exported Config" }])
                });
            } else {
                route.continue();
            }
        });

        // Proceed to click the Export Final Button
        await page.locator('button.btn.primary', { hasText: 'Descargar JSON' }).click();

        const download = await downloadPromise;
        expect(download.suggestedFilename()).toBe('invok_herramientas_publicas.json');
    });


    test('should detect Invok JSON array and dispatch direct import against /api/import/providers', async ({ page }) => {
        // Intercept API Providers load that happens at ToolsBatchComponent init
        await page.route('**/admin/providers', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({ status: 200, json: [] });
            } else {
                route.continue();
            }
        });

        // Intercept target new Backend API import
        let interceptedPostPayload: any = null;
        await page.route('**/api/import/providers', async route => {
            if (route.request().method() === 'POST') {
                interceptedPostPayload = route.request().postDataJSON();
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({ message: "Import created successfully" })
                });
            } else {
                route.continue();
            }
        });

        await page.goto('/tools/batch', { waitUntil: 'networkidle' });

        // Assert the new copy instructing the user
        const textareaLabel = page.getByText('Importador Rápido (Invok JSON u OpenAPI)', { exact: false });
        await expect(textareaLabel).toBeVisible();

        // Fill Invok Provider array format 
        const mockPayload = [
            {
                name: "API Test",
                code: "api-test",
                baseUrl: "https://api.test.com",
                authenticationType: "NONE",
                tools: []
            }
        ];

        const jsonInput = page.locator('#openapi-json');
        await jsonInput.fill(JSON.stringify(mockPayload));

        const importStartBtn = page.locator('button', { hasText: 'Procesar JSON' });
        await importStartBtn.click();

        // Verify Success Message triggered natively bypassing the manual batch form
        const successPrompt = page.locator('.success-msg', { hasText: '¡Importación de Proveedores de Invok exitosa!' });
        await expect(successPrompt).toBeVisible();

        // Verify the exact content transferred matches the string payload 
        expect(interceptedPostPayload).not.toBeNull();
        expect(Array.isArray(interceptedPostPayload)).toBeTruthy();
        expect(interceptedPostPayload[0].name).toBe('API Test');

        // It should eventually redirect to /home 
        // No strict explicit wait mapping so checking route url after ~1.5s
        await page.waitForTimeout(1600);
        await expect(page).toHaveURL(/\/home/);
    });
});
