import { test, expect } from '@playwright/test';

test.describe('Tools Batch Operations & OpenAPI Importer', () => {

    test.beforeEach(async ({ page }) => {
        page.on('console', msg => console.log('BROWSER CONSOLE:', msg.text()));

        // Mock the providers GET request so the page can load
        await page.route('**/admin/providers', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([{ id: 101, name: 'Existing Mock Provider', baseUrl: 'https://api.existing.test' }])
                });
            } else {
                route.continue();
            }
        });

        // Navigate to the batch tools configuration page
        await page.goto('/tools/batch', { waitUntil: 'domcontentloaded' });
    });

    test('should import OpenAPI JSON and generate endpoints', async ({ page }) => {
        const mockOpenApiJson = {
            openapi: "3.0.0",
            servers: [{ url: "https://api.mock.test" }],
            paths: {
                "/users": {
                    get: {
                        summary: "Get Users",
                        operationId: "getUsers",
                        parameters: [{ name: "limit", schema: { type: "integer" } }]
                    },
                    post: {
                        summary: "Create User",
                        operationId: "createUser",
                        requestBody: { content: { "application/json": {} } }
                    }
                }
            }
        };

        // Fill the OpenAPI JSON textarea
        const jsonTextarea = page.locator('textarea#openapi-json');
        await jsonTextarea.fill(JSON.stringify(mockOpenApiJson, null, 2));

        // Click Process JSON
        await page.getByRole('button', { name: 'Procesar JSON' }).click();

        // Verify success message
        await expect(page.locator('.success-msg')).toContainText('¡Éxito! Se detectaron y agregaron 2 endpoints.');

        // Verify base URL was populated
        await expect(page.locator('input[formControlName="baseUrl"]')).toHaveValue('https://api.mock.test');

        // Verify two endpoint cards were created
        const endpointCards = page.locator('.endpoint-card');
        await expect(endpointCards).toHaveCount(2);

        // Verify the details of the first generated endpoint (GET /users)
        await expect(endpointCards.nth(0).locator('.path-input')).toHaveValue('/users');
        await expect(endpointCards.nth(0).locator('.method-badge')).toHaveValue('GET');
        await expect(endpointCards.nth(0).locator('input[formControlName="name"]').first()).toHaveValue('Get Users');
        await expect(endpointCards.nth(0).locator('input[formControlName="code"]')).toHaveValue('getusers');
        await expect(endpointCards.nth(0).locator('input.param-name').first()).toHaveValue('limit');

        // Verify the details of the second generated endpoint (POST /users)
        await expect(endpointCards.nth(1).locator('.path-input')).toHaveValue('/users');
        await expect(endpointCards.nth(1).locator('.method-badge')).toHaveValue('POST');
        await expect(endpointCards.nth(1).locator('input.param-name').first()).toHaveValue('body');
    });

    test('should batch save multiple tools successfully creating a new provider', async ({ page }) => {
        // Mock the backend API call for creating a provider
        await page.route('**/admin/providers', async route => {
            if (route.request().method() === 'POST') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({ id: 99, name: 'New Provider' })
                });
            } else {
                route.continue();
            }
        });

        // Mock the backend API call for creating batch tools
        let interceptedRequestPayload: any = null;

        await page.route('**/admin/tools/api/batch', async route => {
            interceptedRequestPayload = route.request().postDataJSON();
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ message: '1 tools creadas correctamente', status: 200 })
            });
        });

        // Add 1 manual endpoint manually via UI (since we already tested import above)
        await page.locator('input[formControlName="providerName"]').fill('My New Batch Provider');
        await page.locator('input[formControlName="baseUrl"]').fill('https://api.manual.test');

        await page.getByRole('button', { name: '+ Añadir Manualmente' }).click();

        const endpointCards = page.locator('.endpoint-card');
        await expect(endpointCards).toHaveCount(1);

        await endpointCards.nth(0).locator('.path-input').fill('/v1/test');
        await endpointCards.nth(0).locator('input[formControlName="name"]').first().fill('Test Manual');
        await endpointCards.nth(0).locator('input[formControlName="code"]').fill('test-manual');
        await endpointCards.nth(0).locator('textarea[formControlName="description"]').fill('Description manual');

        const submitBtn = page.locator('button[type="submit"]');
        await expect(submitBtn).toBeEnabled();

        // Submit the form
        await submitBtn.click();

        // Debug: Check if error message appeared
        const errorAlert = page.locator('.alert.error');
        if (await errorAlert.isVisible()) {
            console.log('UI ERROR MESSAGE:', await errorAlert.textContent());
        }

        // Verify success message
        await expect(page.getByText('¡Éxito! Se creó el proveedor y 1 herramientas asociadas.')).toBeVisible();

        // Verify the payload sent to the backend had the providerId
        expect(interceptedRequestPayload).toBeTruthy();
        expect(interceptedRequestPayload.length).toBe(1);
        expect(interceptedRequestPayload[0].providerId).toBe(99);
        expect(interceptedRequestPayload[0].endpointPath).toBe('/v1/test');

        // Verify redirection to home
        await page.waitForURL('**/home', { timeout: 3000 });
    });

    test('should batch save tool logic assigning an existing provider', async ({ page }) => {
        // Mock the backend API call for creating batch tools
        let interceptedRequestPayload: any = null;

        await page.route('**/admin/tools/api/batch', async route => {
            interceptedRequestPayload = route.request().postDataJSON();
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ message: '1 tools creadas correctamente', status: 200 })
            });
        });

        // Uncheck the "Create new provider" box
        // Using force: true because sometimes checkboxes have custom CSS overlay that playwight thinks is intercepting
        await page.locator('input[formControlName="isCreatingProvider"]').uncheck({ force: true });

        // Select the existing provider (mocked as ID 101 in beforeEach)
        await page.locator('select[formControlName="providerId"]').selectOption({ value: '101' });

        await page.getByRole('button', { name: '+ Añadir Manualmente' }).click();

        const endpointCards = page.locator('.endpoint-card');
        await expect(endpointCards).toHaveCount(1);

        await endpointCards.nth(0).locator('.path-input').fill('/v1/existing');
        await endpointCards.nth(0).locator('input[formControlName="name"]').first().fill('Test Manual Existing');
        await endpointCards.nth(0).locator('textarea[formControlName="description"]').fill('Description manual existing');

        const submitBtn = page.locator('button[type="submit"]');
        await expect(submitBtn).toBeEnabled();

        // Submit the form
        await submitBtn.click();

        // Verify success message
        await expect(page.getByText('¡Éxito! Se guardaron 1 herramientas asociadas al lote.')).toBeVisible();

        // Verify the payload sent to the backend mapped the providerId=101
        expect(interceptedRequestPayload).toBeTruthy();
        expect(interceptedRequestPayload.length).toBe(1);
        expect(interceptedRequestPayload[0].providerId).toBe(101);
        expect(interceptedRequestPayload[0].endpointPath).toBe('/v1/existing');
    });

});
