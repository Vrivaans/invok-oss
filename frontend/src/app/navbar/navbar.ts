import { Component, OnInit, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../language.service';

@Component({
    selector: 'app-navbar',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterLinkActive, TranslateModule],
    templateUrl: './navbar.html',
    styleUrl: './navbar.scss'
})
export class NavbarComponent implements OnInit {
    private readonly router = inject(Router);

    showMainNavbar = true;

    constructor(public languageService: LanguageService) { }

    ngOnInit() {
        this.refreshShellRoute();
        this.router.events
            .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
            .subscribe(() => this.refreshShellRoute());
    }

    get currentLang(): string {
        return this.languageService.getCurrentLang().toUpperCase();
    }

    toggleLanguage(): void {
        this.languageService.toggleLanguage();
    }

    private refreshShellRoute(): void {
        const url = this.router.url.split(/[?#]/, 1)[0];
        this.showMainNavbar = !(
            url === '/' ||
            url === '/presentation' ||
            url.startsWith('/presentation/') ||
            url === '/landing' ||
            url.startsWith('/landing/')
        );
    }
}
