import { test, expect } from '@playwright/test';

test.describe('Edit Provider Flow', () => {

    test('should open the edit modal and successfully modify an existing provider from the Single Tool view', async ({ page }) => {
        // 1. Go to Tools page
        await page.goto('/tools');

        // Wait for providers drop-down to populate (depends on backend)
        await page.waitForSelector('#providerId', { state: 'attached' });

        // We expect there to be at least one provider as this test relies on pre-existing data
        // If testing in an isolated DB, a provider should be seeded first.
        // For now, we interact with the dropdown. We'll select the last available option.
        const options = await page.locator('#providerId > option').all();

        // Filter out the placeholder option
        const validOptions = options.slice(1);

        if (validOptions.length === 0) {
            test.skip('No providers available to edit, skipping test.');
            return;
        }

        // Select the first valid provider
        const firstProviderValue = await validOptions[0].getAttribute('value');
        await page.locator('#providerId').selectOption(firstProviderValue || '');

        // 2. Click the Edit button
        const editButton = page.locator('button[title="Editar Proveedor Seleccionado"]');
        await expect(editButton).toBeVisible();
        await editButton.click();

        // 3. Verify modal is open and populated
        const modalHeading = page.locator('.modal-content h3');
        await expect(modalHeading).toHaveText('Editar Proveedor');

        // 4. Modify the Provider Name slightly
        const nameInput = page.locator('#editProviderName');
        const originalName = await nameInput.inputValue();

        // To ensure we don't indefinitely grow the name, we remove (E2E Edit) if it's there
        const cleanName = originalName.replace(' (E2E Edit)', '');
        const newName = `${cleanName} (E2E Edit)`;

        await nameInput.fill(newName);

        // 5. Save changes
        const saveButton = page.locator('.modal-content button[type="submit"]');
        await saveButton.click();

        // 6. Verify modal closed
        await expect(page.locator('.modal-overlay')).not.toBeVisible();

        // 7. Verify the dropdown reflects the new name
        // It should now contain our new text in the selected option text
        const selectedText = await page.locator('#providerId option:checked').textContent();
        expect(selectedText).toContain(newName);
    });

});
