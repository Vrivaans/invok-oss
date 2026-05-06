import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly LANG_KEY = 'handsai_lang';

  constructor(private translate: TranslateService) {
    const savedLang = localStorage.getItem(this.LANG_KEY) || 'es';
    this.translate.addLangs(['en', 'es']);
    this.translate.setDefaultLang('es');
    this.use(savedLang);
  }

  use(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem(this.LANG_KEY, lang);
  }

  getCurrentLang(): string {
    return this.translate.currentLang || 'es';
  }

  toggleLanguage(): void {
    const newLang = this.getCurrentLang() === 'es' ? 'en' : 'es';
    this.use(newLang);
  }

  /** Idioma explícito (p. ej. selector ES / EN en login) */
  setLanguage(lang: 'en' | 'es'): void {
    if (lang === 'en' || lang === 'es') {
      this.use(lang);
    }
  }
}
