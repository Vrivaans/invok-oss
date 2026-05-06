import { test, expect } from '@playwright/test';

test.describe('Tools Save Operations', () => {

    test.beforeEach(async ({ page }) => {
        // Navigate to the tools configuration page
        await page.goto('/tools', { waitUntil: 'domcontentloaded' });
    });

    test('should disable submit button and show validation errors on touch', async ({ page }) => {
        const submitBtn = page.locator('button[type="submit"]');

        // Button should be disabled initially because form is invalid
        await expect(submitBtn).toBeDisabled();

        // Touch the name input field and click outside to trigger validation
        const nameInput = page.locator('input[formControlName="name"]');
        await nameInput.click();
        await page.locator('h1').click(); // click outside

        // Verify error message for name
        const nameError = page.locator('.error-msg').filter({ hasText: 'Este campo es obligatorio.' }).first();
        await expect(nameError).toBeVisible();

        // Ensure form is still disabled
        await expect(submitBtn).toBeDisabled();
    });

    test('should save a tool successfully', async ({ page }) => {
        // Mock the backend API call for getting providers
        await page.route('**/admin/providers', async route => {
            if (route.request().method() === 'GET') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify([])
                });
            } else if (route.request().method() === 'POST') {
                await route.fulfill({
                    status: 200,
                    contentType: 'application/json',
                    body: JSON.stringify({ id: 99, name: 'Test Provider' })
                });
            }
        });

        // Mock the backend API call for creating a tool
        await page.route('**/admin/tools/api', async route => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ message: 'Tool creada correctamente', status: 200 })
            });
        });

        // Check the 'Crear nuevo proveedor' checkbox to reveal provider fields
        await page.locator('label[for="isCreatingProvider"]').click();

        // Fill the required fields
        await page.locator('input[formControlName="providerName"]').fill('Test Provider');
        await page.locator('input[formControlName="baseUrl"]').fill('https://api.example.com');
        await page.locator('input[formControlName="name"]').fill('My Test Tool');
        await page.locator('input[formControlName="code"]').fill('TEST_TOOL_01');
        await page.locator('textarea[formControlName="description"]').fill('This is a test description');
        await page.locator('input[formControlName="endpointPath"]').fill('/v1/test');
        await page.locator('select[formControlName="httpMethod"]').selectOption('POST');
        await page.locator('select[formControlName="authenticationType"]').selectOption('NONE');

        const submitBtn = page.locator('button[type="submit"]');
        // Button should now be enabled since all required fields are filled
        await expect(submitBtn).toBeEnabled();

        // Submit the form
        await submitBtn.click();

        // Verify success message
        await expect(page.getByText('Herramienta guardada con éxito.')).toBeVisible();

        // Verify redirection to home
        await page.waitForURL('**/home', { timeout: 3000 });
    });

});
