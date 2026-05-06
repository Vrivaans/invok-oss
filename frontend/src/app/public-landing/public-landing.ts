import { CommonModule } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../language.service';

interface UseCaseVideo {
  id: string;
  titleKey: string;
  descriptionKey: string;
  highlightKey: string;
}

@Component({
  selector: 'app-public-landing',
  standalone: true,
  imports: [CommonModule, RouterLink, TranslateModule],
  templateUrl: './public-landing.html',
  styleUrl: './public-landing.scss'
})
export class PublicLandingComponent {
  private readonly navOffset = 72;
  isNavScrolled = false;

  readonly useCaseVideos: UseCaseVideo[] = [
    {
      id: '7seKdWbP6U0',
      titleKey: 'LANDING.USE_CASES.VIDEOS.ODOO.TITLE',
      descriptionKey: 'LANDING.USE_CASES.VIDEOS.ODOO.DESCRIPTION',
      highlightKey: 'LANDING.USE_CASES.VIDEOS.ODOO.HIGHLIGHT'
    },
    {
      id: 'pvSSlQ3orAQ',
      titleKey: 'LANDING.USE_CASES.VIDEOS.SOCIAL.TITLE',
      descriptionKey: 'LANDING.USE_CASES.VIDEOS.SOCIAL.DESCRIPTION',
      highlightKey: 'LANDING.USE_CASES.VIDEOS.SOCIAL.HIGHLIGHT'
    }
  ];

  constructor(public languageService: LanguageService) { }

  get currentLang(): string {
    return this.languageService.getCurrentLang();
  }

  get nextLangLabel(): string {
    return this.currentLang === 'es' ? 'EN' : 'ES';
  }

  toggleLanguage(): void {
    this.languageService.toggleLanguage();
  }

  scrollToSection(sectionId: string): void {
    const target = document.getElementById(sectionId);
    if (!target) return;

    const top = window.scrollY + target.getBoundingClientRect().top - this.navOffset;
    window.scrollTo({ top, behavior: 'smooth' });
  }

  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  thumbnailUrl(videoId: string): string {
    return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`;
  }

  youtubeUrl(videoId: string): string {
    return `https://youtu.be/${videoId}`;
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    this.isNavScrolled = window.scrollY > 24;
  }
}
