import { test, expect } from '@playwright/test';

test.describe('Home Page - Edit Tool Modal', () => {

    test.beforeEach(async ({ page }) => {
        // Intercept GET requests for stored tools
        await page.route('**/admin/tools/api', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([
                        {
                            id: 1,
                            name: 'Servicio de clima',
                            code: 'api-clima',
                            description: 'Obtiene el clima actual',
                            enabled: true,
                            healthy: true,
                            endpointPath: '/current',
                            httpMethod: 'GET'
                        }
                    ])
                });
            } else {
                route.continue();
            }
        });

        // Intercept GET requests for active tools (MCP)
        await page.route('**/mcp/tools/list', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({ result: { tools: [] } })
                });
            } else {
                route.continue();
            }
        });

        // Navigate to the home page
        await page.goto('/home', { waitUntil: 'domcontentloaded' });
    });

    test('should open edit modal, patch form values and submit PUT request successfully', async ({ page }) => {
        // Intercept PUT requests to simulate successful update
        let interceptedRequestPayload: any = null;
        await page.route('**/admin/tools/api/1', async route => {
            if (route.request().method() === 'PUT') {
                interceptedRequestPayload = route.request().postDataJSON();
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({ id: 1, message: 'Tool updated correctly' })
                });
            } else {
                route.continue();
            }
        });

        // Verify the card exists
        const toolItem = page.locator('.tool-item', { hasText: 'Servicio de clima' });
        await expect(toolItem).toBeVisible();

        // Click the tool card (not the delete button) to open edit modal
        // We use .tool-content since it is safely inside the card and clickable
        await toolItem.locator('.tool-content').click();

        // Verify edit modal is visible and populated with tool details
        const modal = page.locator('.modal-content', { hasText: 'Editar Herramienta' });
        await expect(modal).toBeVisible();
        await expect(page.locator('input[formControlName="name"]')).toHaveValue('Servicio de clima');
        await expect(page.locator('input[formControlName="endpointPath"]')).toHaveValue('/current');
        await expect(page.locator('textarea[formControlName="description"]')).toHaveValue('Obtiene el clima actual');

        // Capture screenshot of the modal
        await page.screenshot({ path: '/Users/ivanv/.gemini/antigravity/brain/85d54339-232d-4c3e-9341-3a222d38e5d6/edit_modal_premium_screenshot.png' });

        // Modify the description
        const descriptionField = page.locator('textarea[formControlName="description"]');
        await descriptionField.fill('Obtiene el clima actual [MODIFICADO]');

        const submitBtn = page.locator('button[type="submit"]');
        await expect(submitBtn).toBeEnabled();

        // Submit the form
        await submitBtn.click();

        // Verify modal closed (using timeout)
        await expect(modal).not.toBeVisible({ timeout: 5000 });

        // Verify the payload sent to the backend has the modified description
        expect(interceptedRequestPayload).toBeTruthy();
        expect(interceptedRequestPayload.description).toBe('Obtiene el clima actual [MODIFICADO]');
        expect(interceptedRequestPayload.name).toBe('Servicio de clima');
        expect(interceptedRequestPayload.endpointPath).toBe('/current');
        expect(interceptedRequestPayload.httpMethod).toBe('GET');
        expect(interceptedRequestPayload.enabled).toBe(true);
    });
});
