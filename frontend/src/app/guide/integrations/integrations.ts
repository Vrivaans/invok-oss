import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-integrations-guide',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './integrations.html',
  styleUrl: './integrations.scss'
})
export class IntegrationsGuideComponent implements OnInit {
  private readonly http = inject(HttpClient);

  loading = true;
  error = '';
  guide: any = null;

  ngOnInit(): void {
    this.http.get('/api/guide').subscribe({
      next: (res) => {
        this.guide = res;
        this.loading = false;
      },
      error: () => {
        this.error = 'GUIDE.DOCS.LOAD_ERROR';
        this.loading = false;
      }
    });
  }

  asList(value: unknown): string[] {
    return Array.isArray(value) ? value as string[] : [];
  }

  advancedCase(key: string): string[] {
    const advanced = this.guide?.['advanced_cases'];
    if (!advanced || typeof advanced !== 'object') return [];
    const value = (advanced as Record<string, unknown>)[key];
    return this.asList(value);
  }

  objectKeys(obj: unknown): string[] {
    if (!obj || typeof obj !== 'object') return [];
    return Object.keys(obj as Record<string, unknown>);
  }
}
