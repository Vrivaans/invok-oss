import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-mcp-guide',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './mcp.html',
  styleUrls: ['./mcp.scss']
})
export class McpGuideComponent {
  /** Release: https://github.com/Vrivaans/handsai-bridge/releases */
  private static readonly BRIDGE_RELEASE = 'v1.0.0';
  private static readonly BRIDGE_DOWNLOAD_BASE =
    `https://github.com/Vrivaans/handsai-bridge/releases/download/${McpGuideComponent.BRIDGE_RELEASE}`;

  readonly bridgeDownloads: { labelKey: string; file: string }[] = [
    { labelKey: 'MCP.BRIDGE_WINDOWS', file: `handsai-bridge-${McpGuideComponent.BRIDGE_RELEASE}-windows-amd64.zip` },
    { labelKey: 'MCP.BRIDGE_LINUX', file: `handsai-bridge-${McpGuideComponent.BRIDGE_RELEASE}-linux-amd64.tar.gz` },
    { labelKey: 'MCP.BRIDGE_MAC_ARM', file: `handsai-bridge-${McpGuideComponent.BRIDGE_RELEASE}-darwin-arm64.tar.gz` },
    { labelKey: 'MCP.BRIDGE_MAC_INTEL', file: `handsai-bridge-${McpGuideComponent.BRIDGE_RELEASE}-darwin-amd64.tar.gz` }
  ];

  bridgeDownloadUrl(file: string): string {
    return `${McpGuideComponent.BRIDGE_DOWNLOAD_BASE}/${file}`;
  }
}
