import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

interface TutorialVideo {
  id: string;
  titleKey: string;
  descriptionKey: string;
}

@Component({
  selector: 'app-tutorials-guide',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './tutorials.html',
  styleUrl: './tutorials.scss'
})
export class TutorialsGuideComponent {
  readonly videos: TutorialVideo[] = [
    {
      id: 'bXTKyPiqpLc',
      titleKey: 'TUTORIALS.VIDEOS.EXPORT_IMPORT.TITLE',
      descriptionKey: 'TUTORIALS.VIDEOS.EXPORT_IMPORT.DESCRIPTION'
    },
    {
      id: '7seKdWbP6U0',
      titleKey: 'TUTORIALS.VIDEOS.ODOO_EXECUTION.TITLE',
      descriptionKey: 'TUTORIALS.VIDEOS.ODOO_EXECUTION.DESCRIPTION'
    }
  ];

  thumbnailUrl(videoId: string): string {
    return `https://img.youtube.com/vi/${videoId}/hqdefault.jpg`;
  }

  youtubeUrl(videoId: string): string {
    return `https://youtu.be/${videoId}`;
  }
}
