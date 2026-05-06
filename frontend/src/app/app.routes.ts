import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { ToolsComponent } from './tools/tools';
import { McpGuideComponent } from './guide/mcp/mcp';
import { IntegrationsGuideComponent } from './guide/integrations/integrations';
import { TutorialsGuideComponent } from './guide/tutorials/tutorials';
import { PublicLandingComponent } from './public-landing/public-landing';

export const routes: Routes = [
    { path: '', redirectTo: 'presentation', pathMatch: 'full' },
    { path: 'presentation', component: PublicLandingComponent },
    { path: 'landing', redirectTo: 'presentation', pathMatch: 'full' },
    { path: 'guide/mcp', component: McpGuideComponent },
    { path: 'guide/integrations', component: IntegrationsGuideComponent },
    { path: 'tutoriales', component: TutorialsGuideComponent },
    { path: 'tutorials', component: TutorialsGuideComponent },
    { path: 'home', component: HomeComponent },
    { path: 'tools', component: ToolsComponent },
    { path: '**', redirectTo: '/home' }
];
