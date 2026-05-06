<!DOCTYPE html>
<html lang="en" class="lang-en">

<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Invok — The Bridge Between AI and the World</title>
  <link
    href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Mono:wght@300;400;500&family=Instrument+Sans:wght@400;500;600&display=swap"
    rel="stylesheet">
  <style>
    *,
    *::before,
    *::after {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    :root {
      --bg: #0a0a0a;
      --surface: #111111;
      --surface2: #1a1a1a;
      --border: #222222;
      --accent: #c8f04a;
      --accent2: #4af0c8;
      --text: #f0f0ec;
      --muted: #666660;
      --danger: #f04a4a;
    }

    html {
      scroll-behavior: smooth;
    }

    body {
      background: var(--bg);
      color: var(--text);
      font-family: 'Instrument Sans', sans-serif;
      overflow-x: hidden;
      cursor: none;
    }


    /* Language Toggle */
    html.lang-en .lang-es {
      display: none !important;
    }

    html.lang-es .lang-en {
      display: none !important;
    }

    /* Custom cursor */
    .cursor {
      position: fixed;
      width: 10px;
      height: 10px;
      background: var(--accent);
      border-radius: 50%;
      pointer-events: none;
      z-index: 9999;
      transform: translate(-50%, -50%);
      transition: transform 0.1s, width 0.2s, height 0.2s, background 0.2s;
      mix-blend-mode: exclusion;
    }

    .cursor.hovering {
      width: 40px;
      height: 40px;
    }

    /* Noise overlay */
    body::before {
      content: '';
      position: fixed;
      inset: 0;
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
      pointer-events: none;
      z-index: 1000;
      opacity: 0.4;
    }

    /* Nav */
    nav {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 100;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1.5rem 3rem;
      border-bottom: 1px solid transparent;
      transition: border-color 0.3s, background 0.3s;
    }

    nav.scrolled {
      border-color: var(--border);
      background: rgba(10, 10, 10, 0.9);
      backdrop-filter: blur(12px);
    }

    .nav-logo {
      font-family: 'DM Mono', monospace;
      font-size: 1.1rem;
      font-weight: 500;
      letter-spacing: -0.02em;
      color: var(--accent);
    }

    .nav-links {
      display: flex;
      gap: 2.5rem;
      list-style: none;
    }

    .nav-links a {
      font-size: 0.85rem;
      color: var(--muted);
      text-decoration: none;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      transition: color 0.2s;
    }

    .nav-links a:hover {
      color: var(--text);
    }

    /* Hero */
    .hero {
      min-height: 100vh;
      display: grid;
      place-items: center;
      padding: 8rem 3rem 4rem;
      position: relative;
      overflow: hidden;
    }

    .hero-bg {
      position: absolute;
      inset: 0;
      background:
        radial-gradient(ellipse 60% 50% at 20% 60%, rgba(200, 240, 74, 0.06) 0%, transparent 70%),
        radial-gradient(ellipse 40% 60% at 80% 30%, rgba(74, 240, 200, 0.04) 0%, transparent 70%);
    }

    .hero-grid {
      position: absolute;
      inset: 0;
      background-image:
        linear-gradient(var(--border) 1px, transparent 1px),
        linear-gradient(90deg, var(--border) 1px, transparent 1px);
      background-size: 60px 60px;
      mask-image: radial-gradient(ellipse 80% 80% at 50% 50%, black 30%, transparent 100%);
      opacity: 0.3;
    }

    .hero-content {
      position: relative;
      max-width: 900px;
      text-align: center;
      z-index: 1;
    }

    .hero-badge {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.4rem 1rem;
      border: 1px solid var(--border);
      border-radius: 100px;
      font-family: 'DM Mono', monospace;
      font-size: 0.72rem;
      color: var(--muted);
      letter-spacing: 0.08em;
      text-transform: uppercase;
      margin-bottom: 2.5rem;
      animation: fadeUp 0.8s ease both;
    }

    .hero-badge .dot {
      width: 6px;
      height: 6px;
      background: var(--accent);
      border-radius: 50%;
      animation: pulse 2s ease infinite;
    }

    @keyframes pulse {

      0%,
      100% {
        opacity: 1;
        transform: scale(1);
      }

      50% {
        opacity: 0.5;
        transform: scale(0.8);
      }
    }

    .hero h1 {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(3.5rem, 8vw, 7rem);
      line-height: 1;
      letter-spacing: -0.03em;
      margin-bottom: 1.5rem;
      animation: fadeUp 0.8s 0.1s ease both;
    }

    .hero h1 em {
      font-style: italic;
      color: var(--accent);
    }

    .hero-sub {
      font-size: 1.15rem;
      color: var(--muted);
      max-width: 560px;
      margin: 0 auto 1.25rem;
      line-height: 1.7;
      animation: fadeUp 0.8s 0.2s ease both;
    }

    .hero-hook {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(1.15rem, 2.5vw, 1.45rem);
      color: var(--accent);
      font-style: italic;
      max-width: 560px;
      margin: 0 auto 3rem;
      line-height: 1.35;
      animation: fadeUp 0.8s 0.25s ease both;
    }

    .hero-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      animation: fadeUp 0.8s 0.3s ease both;
    }

    .btn-primary {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.85rem 2rem;
      background: var(--accent);
      color: #0a0a0a;
      font-weight: 600;
      font-size: 0.9rem;
      text-decoration: none;
      border-radius: 4px;
      transition: transform 0.2s, box-shadow 0.2s;
    }

    .btn-primary:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(200, 240, 74, 0.3);
    }

    .btn-secondary {
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.85rem 2rem;
      border: 1px solid var(--border);
      color: var(--text);
      font-size: 0.9rem;
      text-decoration: none;
      border-radius: 4px;
      transition: border-color 0.2s, background 0.2s;
    }

    .btn-secondary:hover {
      border-color: var(--muted);
      background: var(--surface);
    }

    @keyframes fadeUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }

      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    /* Flow diagram */
    .flow {
      padding: 6rem 3rem;
      max-width: 1100px;
      margin: 0 auto;
    }

    .section-label {
      font-family: 'DM Mono', monospace;
      font-size: 0.72rem;
      color: var(--accent);
      letter-spacing: 0.12em;
      text-transform: uppercase;
      margin-bottom: 1rem;
    }

    .section-title {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(2rem, 4vw, 3.2rem);
      line-height: 1.1;
      margin-bottom: 1.5rem;
      letter-spacing: -0.02em;
    }

    .section-desc {
      color: var(--muted);
      font-size: 1.05rem;
      line-height: 1.7;
      max-width: 540px;
      margin-bottom: 4rem;
    }

    .flow-diagram {
      display: grid;
      grid-template-columns: 1fr auto 1fr auto 1fr auto 1fr;
      align-items: center;
      gap: 0;
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 12px;
      padding: 2.5rem;
      overflow: hidden;
      position: relative;
    }

    .flow-diagram::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, transparent, var(--accent), transparent);
      opacity: 0.5;
    }

    .flow-node {
      text-align: center;
      padding: 1.5rem 1rem;
    }

    .flow-node .icon {
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 0.75rem;
      font-size: 1.4rem;
    }

    .flow-node.llm .icon {
      background: rgba(74, 240, 200, 0.1);
      border: 1px solid rgba(74, 240, 200, 0.2);
    }

    .flow-node.bridge .icon {
      background: rgba(200, 240, 74, 0.1);
      border: 1px solid rgba(200, 240, 74, 0.2);
    }

    .flow-node.invok .icon {
      background: rgba(200, 240, 74, 0.15);
      border: 1px solid var(--accent);
    }

    .flow-node.api .icon {
      background: rgba(240, 74, 74, 0.1);
      border: 1px solid rgba(240, 74, 74, 0.2);
    }

    .flow-node .label {
      font-family: 'DM Mono', monospace;
      font-size: 0.72rem;
      color: var(--muted);
      letter-spacing: 0.06em;
      text-transform: uppercase;
      margin-bottom: 0.25rem;
    }

    .flow-node .name {
      font-weight: 600;
      font-size: 0.95rem;
    }

    .flow-arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.3rem;
      padding: 0 0.5rem;
    }

    .flow-arrow .line {
      width: 40px;
      height: 1px;
      background: linear-gradient(90deg, var(--border), var(--muted));
      position: relative;
    }

    .flow-arrow .line::after {
      content: '▶';
      position: absolute;
      right: -8px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 0.5rem;
      color: var(--muted);
    }

    .flow-arrow .protocol {
      font-family: 'DM Mono', monospace;
      font-size: 0.6rem;
      color: var(--accent);
      letter-spacing: 0.06em;
      white-space: nowrap;
    }

    /* The key insight */
    .insight {
      margin-top: 2rem;
      padding: 1.5rem 2rem;
      background: rgba(200, 240, 74, 0.04);
      border: 1px solid rgba(200, 240, 74, 0.15);
      border-radius: 8px;
      display: flex;
      gap: 1rem;
      align-items: flex-start;
    }

    .insight .bullet {
      color: var(--accent);
      font-size: 1.2rem;
      margin-top: 0.1rem;
      flex-shrink: 0;
    }

    .insight p {
      color: var(--muted);
      font-size: 0.95rem;
      line-height: 1.6;
    }

    .insight p strong {
      color: var(--text);
    }

    /* <span class="lang-en">Features</span><span class="lang-es">Características</span> grid */
    .features {
      padding: 6rem 3rem;
      background: var(--surface);
      border-top: 1px solid var(--border);
      border-bottom: 1px solid var(--border);
    }

    .features-inner {
      max-width: 1100px;
      margin: 0 auto;
    }

    .features-grid {
      display: grid;
      /* 2 cols: evita huecos con 4 tarjetas (2×2) o con 2 tarjetas (precios) */
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 1px;
      background: var(--border);
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
      margin-top: 4rem;
    }

    /* 4 pasos en una fila en viewports anchos (sin celdas vacías) */
    @media (min-width: 1024px) {
      .flow .features-grid {
        grid-template-columns: repeat(4, minmax(0, 1fr));
      }
    }

    /* 5 tarjetas en 2 cols: la última ocupa toda la fila (sin celda vacía) */
    #features .features-grid .feature-card:last-child {
      grid-column: 1 / -1;
    }

    .feature-card {
      background: var(--surface);
      padding: 2.5rem;
      transition: background 0.2s;
      position: relative;
      overflow: hidden;
    }

    .feature-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 2px;
      background: var(--accent);
      transform: scaleX(0);
      transform-origin: left;
      transition: transform 0.3s;
    }

    .feature-card:hover::before {
      transform: scaleX(1);
    }

    .feature-card:hover {
      background: var(--surface2);
    }

    .feature-icon {
      font-size: 1.8rem;
      margin-bottom: 1.25rem;
    }

    .feature-card h3 {
      font-size: 1.05rem;
      font-weight: 600;
      margin-bottom: 0.75rem;
      letter-spacing: -0.01em;
    }

    .feature-card p {
      color: var(--muted);
      font-size: 0.9rem;
      line-height: 1.65;
    }

    /* <span class="lang-en">Security</span><span class="lang-es">Seguridad</span> section */
    .security {
      padding: 6rem 3rem;
      max-width: 1100px;
      margin: 0 auto;
    }

    .security-layout {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 5rem;
      align-items: center;
      margin-top: 4rem;
    }

    .security-list {
      list-style: none;
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }

    .security-list li {
      display: flex;
      gap: 1rem;
      align-items: flex-start;
      padding: 1.25rem;
      border: 1px solid var(--border);
      border-radius: 8px;
      transition: border-color 0.2s, background 0.2s;
    }

    .security-list li:hover {
      border-color: rgba(200, 240, 74, 0.3);
      background: rgba(200, 240, 74, 0.02);
    }

    .security-list .check {
      color: var(--accent);
      font-size: 0.9rem;
      margin-top: 0.15rem;
      flex-shrink: 0;
      font-family: 'DM Mono', monospace;
    }

    .security-list .text h4 {
      font-size: 0.95rem;
      font-weight: 600;
      margin-bottom: 0.3rem;
    }

    .security-list .text p {
      font-size: 0.85rem;
      color: var(--muted);
      line-height: 1.5;
    }

    .code-block {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
    }

    .code-header {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.9rem 1.25rem;
      border-bottom: 1px solid var(--border);
      background: var(--surface2);
    }

    .code-header .dots {
      display: flex;
      gap: 0.4rem;
    }

    .code-header .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
    }

    .code-header .dot:nth-child(1) {
      background: #ff5f57;
    }

    .code-header .dot:nth-child(2) {
      background: #febc2e;
    }

    .code-header .dot:nth-child(3) {
      background: #28c840;
    }

    .code-header .filename {
      font-family: 'DM Mono', monospace;
      font-size: 0.72rem;
      color: var(--muted);
      margin-left: 0.5rem;
    }

    .code-body {
      padding: 1.5rem;
      font-family: 'DM Mono', monospace;
      font-size: 0.8rem;
      line-height: 1.8;
      color: #a0a09a;
    }

    .code-body .comment {
      color: #555550;
    }

    .code-body .key {
      color: var(--accent2);
    }

    .code-body .value {
      color: var(--accent);
    }

    .code-body .redacted {
      color: var(--danger);
    }

    .code-body .string {
      color: #e0c87a;
    }

    /* <span class="lang-en">Philosophy</span><span class="lang-es">Filosofía</span> section */
    .philosophy {
      padding: 6rem 3rem;
      background: var(--surface);
      border-top: 1px solid var(--border);
    }

    .philosophy-inner {
      max-width: 900px;
      margin: 0 auto;
      text-align: center;
    }

    .philosophy h2 {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(2.2rem, 4vw, 3.5rem);
      line-height: 1.15;
      letter-spacing: -0.02em;
      margin-bottom: 3rem;
    }

    .philosophy h2 em {
      font-style: italic;
      color: var(--accent);
    }

    .pillars {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      margin-top: 3rem;
      text-align: left;
      max-width: 900px;
      margin-left: auto;
      margin-right: auto;
    }

    .pillar {
      padding: 2rem;
      border: 1px solid var(--border);
      border-radius: 8px;
    }

    .pillar .num {
      font-family: 'DM Serif Display', serif;
      font-size: 3rem;
      color: var(--border);
      line-height: 1;
      margin-bottom: 1rem;
    }

    .pillar h4 {
      font-size: 0.95rem;
      font-weight: 600;
      margin-bottom: 0.5rem;
    }

    .pillar p {
      font-size: 0.85rem;
      color: var(--muted);
      line-height: 1.6;
    }

    /* Reusable API packs */
    .packs {
      padding: 6rem 3rem;
      background: var(--bg);
      border-top: 1px solid var(--border);
    }

    .packs-inner {
      max-width: 1100px;
      margin: 0 auto;
    }

    .packs-json-wrap {
      margin-top: 2.5rem;
      max-width: 520px;
    }

    .packs-flow {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 1px;
      background: var(--border);
      border: 1px solid var(--border);
      border-radius: 12px;
      overflow: hidden;
      margin-top: 3rem;
    }

    .packs-step {
      background: var(--surface);
      padding: 2rem 1.5rem;
      text-align: left;
      transition: background 0.2s;
    }

    .packs-step:hover {
      background: var(--surface2);
    }

    .packs-step .step-num {
      font-family: 'DM Mono', monospace;
      font-size: 0.72rem;
      color: var(--accent);
      letter-spacing: 0.08em;
      text-transform: uppercase;
      margin-bottom: 0.75rem;
    }

    .packs-step h3 {
      font-size: 1.05rem;
      font-weight: 600;
      margin-bottom: 0;
      letter-spacing: -0.01em;
      line-height: 1.35;
    }

    .packs .insight.packs-insight {
      margin-top: 2.5rem;
      max-width: 640px;
      margin-left: auto;
      margin-right: auto;
    }

    .packs .insight.packs-insight p strong {
      color: var(--text);
      font-size: 1rem;
      font-weight: 600;
    }

    .packs-tagline {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(1.35rem, 3vw, 1.85rem);
      color: var(--text);
      text-align: center;
      margin-top: 2.5rem;
      letter-spacing: -0.02em;
    }

    .packs-tagline em {
      font-style: italic;
      color: var(--accent);
    }

    /* Stack */
    .stack {
      padding: 6rem 3rem;
      max-width: 1100px;
      margin: 0 auto;
    }

    .tech-pills {
      display: flex;
      flex-wrap: wrap;
      gap: 0.75rem;
      margin-top: 3rem;
    }

    .tech-pill {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.5rem 1rem;
      border: 1px solid var(--border);
      border-radius: 100px;
      font-family: 'DM Mono', monospace;
      font-size: 0.78rem;
      color: var(--muted);
      transition: border-color 0.2s, color 0.2s;
    }

    .tech-pill:hover {
      border-color: var(--accent);
      color: var(--text);
    }

    .tech-pill .dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: var(--accent);
    }

    /* CTA */
    .cta {
      padding: 8rem 3rem;
      text-align: center;
      position: relative;
      overflow: hidden;
    }

    .cta::before {
      content: '';
      position: absolute;
      inset: 0;
      background: radial-gradient(ellipse 60% 80% at 50% 100%, rgba(200, 240, 74, 0.07) 0%, transparent 70%);
    }

    .cta h2 {
      font-family: 'DM Serif Display', serif;
      font-size: clamp(2.5rem, 5vw, 4rem);
      letter-spacing: -0.02em;
      margin-bottom: 1.5rem;
      position: relative;
    }

    .cta h2 em {
      font-style: italic;
      color: var(--accent);
    }

    .cta p {
      color: var(--muted);
      font-size: 1.1rem;
      max-width: 480px;
      margin: 0 auto 3rem;
      line-height: 1.7;
      position: relative;
    }

    .cta-actions {
      position: relative;
      display: flex;
      gap: 1rem;
      justify-content: center;
    }

    /* Footer */
    footer {
      padding: 2rem 3rem;
      border-top: 1px solid var(--border);
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    footer .logo {
      font-family: 'DM Mono', monospace;
      font-size: 0.85rem;
      font-weight: 500;
      letter-spacing: -0.02em;
      color: var(--accent);
    }

    footer .logo span {
      color: var(--text);
    }

    footer p {
      font-size: 0.8rem;
      color: var(--muted);
      font-family: 'DM Mono', monospace;
    }

    /* Scrollbar */
    ::-webkit-scrollbar {
      width: 4px;
    }

    ::-webkit-scrollbar-track {
      background: var(--bg);
    }

    ::-webkit-scrollbar-thumb {
      background: var(--border);
      border-radius: 4px;
    }

    /* Responsive */
    @media (max-width: 768px) {
      nav {
        padding: 1.25rem 1.5rem;
      }

      .nav-links {
        display: none;
      }

      .hero {
        padding: 7rem 1.5rem 3rem;
      }

      .flow,
      .features,
      .packs,
      .security,
      .philosophy,
      .stack,
      .cta {
        padding: 4rem 1.5rem;
      }

      .packs-flow {
        grid-template-columns: 1fr;
      }

      .flow-diagram {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .flow-arrow {
        transform: rotate(90deg);
      }

      .features-grid {
        grid-template-columns: 1fr;
      }

      .security-layout {
        grid-template-columns: 1fr;
        gap: 3rem;
      }

      .pillars {
        grid-template-columns: 1fr;
      }

      footer {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
      }
    }
  </style>
</head>

<body>

  <div class="cursor" id="cursor"></div>

  <nav id="nav">
    <div class="nav-logo">Inv<span style="color:var(--text)">ok</span></div>
    <ul class="nav-links">
      <li><a href="#philosophy"><span class="lang-en">Philosophy</span><span class="lang-es">Filosofía</span></a></li>
      <li><a href="#features"><span class="lang-en">Features</span><span class="lang-es">Características</span></a></li>
      <li><a href="#packs"><span class="lang-en">Packs</span><span class="lang-es">Packs</span></a></li>
      <li><a href="#security"><span class="lang-en">Security</span><span class="lang-es">Seguridad</span></a></li>
      <li><a href="#how"><span class="lang-en">How it works</span><span class="lang-es">Cómo funciona</span></a></li>
      <li><button id="lang-toggle" onclick="toggleLanguage()"
          style="background:none;border:none;color:var(--accent);font-family:'DM Mono',monospace;cursor:pointer;font-size:0.85rem;letter-spacing:0.05em;padding-top:2px;outline:none;">ES</button>
      </li>
    </ul>
  </nav>

 <!-- HERO -->
<section class="hero">
  <div class="hero-bg"></div>
  <div class="hero-grid"></div>
  <div class="hero-content">

    <div class="hero-badge">
      <span class="dot"></span>
      <span class="lang-en">Early access</span><span class="lang-es">Acceso anticipado</span>
    </div>

    <h1>
      <span class="lang-en">Turn any API into tools<br>
      your AI can use</span><span class="lang-es">Convierte cualquier API en herramientas<br>
      que tu IA pueda usar</span>
    </h1>

    <p class="hero-sub">
      <span class="lang-en">Register any API once. Turn it into reusable tools.<br>
      Your AI agent can use them — or share them with anyone.</span><span class="lang-es">Registrá cualquier API una vez. Convertila en herramientas reutilizables.<br>
      Tu agente las usa — o las podés compartir con cualquiera.</span>
    </p>

    <p class="hero-hook">
      <span class="lang-en">Never integrate the same API twice.</span><span class="lang-es">Nunca integrés la misma API dos veces.</span>
    </p>

    <div class="hero-actions">
      <a href="https://useinvok.run/" class="btn-primary" target="_blank" rel="noopener noreferrer"><span class="lang-en">Try it now →</span><span class="lang-es">Pruébalo ahora →</span></a>
      <a href="#features" class="btn-secondary"><span class="lang-en">See it in action</span><span class="lang-es">Ver en acción</span></a>
    </div>

  </div>
</section>


<!-- HOW IT WORKS -->
<section class="flow" id="how">
  <p class="section-label"><span class="lang-en">How it works</span><span class="lang-es">Cómo funciona</span></p>
  <h2 class="section-title"><span class="lang-en">From API to action in minutes</span><span class="lang-es">De la API a la acción en minutos</span></h2>
  <p class="section-desc">
    <span class="lang-en">No setup headaches. Just connect, generate, and run.</span><span class="lang-es">Sin dolores de configuración. Solo conecta, genera y ejecuta.</span>
  </p>

  <div class="features-grid">

    <div class="feature-card">
      <div class="feature-icon">📄</div>
      <h3><span class="lang-en">1. Paste API docs</span><span class="lang-es">1. Pega la documentación de la API</span></h3>
      <p><span class="lang-en">Import OpenAPI or documentation. Invok understands the structure automatically.</span><span class="lang-es">Importa OpenAPI u otra documentación. Invok entiende la estructura automáticamente.</span></p>
    </div>

    <div class="feature-card">
      <div class="feature-icon">🔑</div>
      <h3><span class="lang-en">2. Add your API key</span><span class="lang-es">2. Añade tu clave de API</span></h3>
      <p><span class="lang-en">Your credentials stay secure. The agent never sees them.</span><span class="lang-es">Tus credenciales siguen seguras. El agente nunca las ve.</span></p>
    </div>

    <div class="feature-card">
      <div class="feature-icon">⚡</div>
      <h3><span class="lang-en">3. Generate tools</span><span class="lang-es">3. Genera herramientas</span></h3>
      <p><span class="lang-en">Invok converts endpoints into tools your AI can call instantly.</span><span class="lang-es">Invok convierte los endpoints en herramientas que tu IA puede invocar al instante.</span></p>
    </div>

    <div class="feature-card">
      <div class="feature-icon">🤖</div>
      <h3><span class="lang-en">4. Let your agent act</span><span class="lang-es">4. Deja que tu agente actúe</span></h3>
      <p><span class="lang-en">Your agent uses natural language to execute real actions.</span><span class="lang-es">Tu agente usa lenguaje natural para ejecutar acciones reales.</span></p>
    </div>

  </div>

  <div class="insight" style="margin-top:2.5rem;">
    <span class="bullet">💡</span>
    <p><span class="lang-en">Tools can also be imported from external packs — no need to build everything from scratch.</span><span class="lang-es">Las herramientas también pueden importarse desde packs externos — no hace falta construir todo desde cero.</span></p>
  </div>
</section>


<!-- USE CASES (CRITICAL) -->
<section class="features" id="features">
  <div class="features-inner">
    <p class="section-label"><span class="lang-en">Use cases</span><span class="lang-es">Casos de uso</span></p>
    <h2 class="section-title">
      <span class="lang-en">Real actions. Not just responses.</span><span class="lang-es">Acciones reales. No solo respuestas.</span>
    </h2>
    <p class="section-desc">
      <span class="lang-en">Your AI doesn’t just analyze data — it interacts with real systems.</span><span class="lang-es">Tu IA no solo analiza datos: interactúa con sistemas reales.</span>
    </p>

    <div class="features-grid">

      <div class="feature-card">
        <div class="feature-icon">📋</div>
        <h3><span class="lang-en">Manage Trello automatically</span><span class="lang-es">Gestiona Trello automáticamente</span></h3>
        <p><span class="lang-en">Create cards, move tasks, and update boards using natural language.</span><span class="lang-es">Crea tarjetas, mueve tareas y actualiza tableros con lenguaje natural.</span></p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">📈</div>
        <h3><span class="lang-en">Operate your CRM (Odoo)</span><span class="lang-es">Opera tu CRM (Odoo)</span></h3>
        <p><span class="lang-en">Create leads, update records, and manage pipelines without integrations.</span><span class="lang-es">Crea leads, actualiza registros y gestiona embudos sin integraciones a medida.</span></p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">🌐</div>
        <h3><span class="lang-en">Deploy infrastructure</span><span class="lang-es">Despliega infraestructura</span></h3>
        <p><span class="lang-en">Spin up VPS instances directly from your AI agent.</span><span class="lang-es">Arranca instancias VPS directamente desde tu agente de IA.</span></p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">💬</div>
        <h3><span class="lang-en">Automate social media</span><span class="lang-es">Automatiza redes sociales</span></h3>
        <p><span class="lang-en">Post, reply, and manage accounts like Bluesky or others.</span><span class="lang-es">Publica, responde y gestiona cuentas como Bluesky u otras.</span></p>
      </div>

      <div class="feature-card">
        <div class="feature-icon">🧩</div>
        <h3><span class="lang-en">Shareable integrations</span><span class="lang-es">Integraciones compartibles</span></h3>
        <p><span class="lang-en">Export and import full API toolsets. Build once, reuse forever.</span><span class="lang-es">Exportá e importá integraciones completas. Construí una vez, reutilizá para siempre.</span></p>
      </div>

    </div>
  </div>
</section>


<!-- REUSABLE API PACKS -->
<section class="packs" id="packs">
  <div class="packs-inner">
    <p class="section-label"><span class="lang-en">Reusable API packs</span><span class="lang-es">Packs de API reutilizables</span></p>
    <h2 class="section-title"><span class="lang-en">Build once. Share everywhere.</span><span class="lang-es">Creá una vez. Usá en todos lados.</span></h2>
    <p class="section-desc">
      <span class="lang-en">Invok lets you export your integrations as portable JSON packs.
      Anyone can import them, add their API key, and instantly unlock the same capabilities.</span><span class="lang-es">Invok te permite exportar tus integraciones como packs JSON portables.
      Cualquiera puede importarlos, agregar su API key y tener las mismas capacidades al instante.</span>
    </p>

    <div class="packs-json-wrap">
      <div class="code-block">
        <div class="code-header">
          <div class="dots">
            <span class="dot"></span><span class="dot"></span><span class="dot"></span>
          </div>
          <span class="filename">cubepath-pack.json</span>
        </div>
        <div class="code-body packs-json-code">
{<br>
&nbsp;&nbsp;<span class="key">"name"</span>: <span class="string">"CubePath VPS"</span>,<br>
&nbsp;&nbsp;<span class="key">"tools"</span>: [<br>
&nbsp;&nbsp;&nbsp;&nbsp;<span class="string">"create-server"</span>,<br>0
&nbsp;&n10bsp;&nbsp;&nbsp;<span class="string">"list-vps"</span>,<br>
&nbsp;&nbsp;&nbsp;&nbsp;<span class="string">"metrics"</span><br>
&nbsp;&nbsp;]<br>
}
        </div>
      </div>
    </div>

    <div class="packs-flow">
      <div class="packs-step">
        <div class="step-num"><span class="lang-en">1 · Create</span><span class="lang-es">1 · Crear</span></div>
        <h3><span class="lang-en">Connect any API and generate tools</span><span class="lang-es">Conectá cualquier API y generá herramientas</span></h3>
      </div>
      <div class="packs-step">
        <div class="step-num"><span class="lang-en">2 · Export</span><span class="lang-es">2 · Exportar</span></div>
        <h3><span class="lang-en">Download as reusable JSON pack</span><span class="lang-es">Descargá un pack JSON reutilizable</span></h3>
      </div>
      <div class="packs-step">
        <div class="step-num"><span class="lang-en">3 · Import</span><span class="lang-es">3 · Importar</span></div>
        <h3><span class="lang-en">Add API key → your agent is ready</span><span class="lang-es">Agregá la API key → tu agente listo</span></h3>
      </div>
    </div>

    <div class="insight packs-insight">
      <span class="bullet">🎯</span>
      <p>
        <span class="lang-en"><strong>Import only what you need</strong><br>
        Not bloated integrations. Just the tools your agent actually uses.</span><span class="lang-es"><strong>Importá solo lo que necesitás</strong><br>
        Sin integraciones de más. Solo las herramientas que tu agente realmente usa.</span>
      </p>
    </div>

    <p class="packs-tagline">
      <span class="lang-en">APIs become assets. <em>Not work.</em></span><span class="lang-es">Las APIs son activos. <em>No trabajo repetido.</em></span>
    </p>
  </div>
</section>


<!-- VALUE PROPOSITION -->
<section class="philosophy" id="philosophy">
  <div class="philosophy-inner">

    <p class="section-label"><span class="lang-en">Why Invok</span><span class="lang-es">Por qué Invok</span></p>

    <h2>
      <span class="lang-en">Your AI already knows what to do.<br>
      <em>Invok lets it do it.</em></span><span class="lang-es">Tu IA ya sabe qué hacer.<br>
      <em>Invok le permite hacerlo.</em></span>
    </h2>

    <p style="color:var(--muted);max-width:600px;margin:0 auto;">
      <span class="lang-en">Without Invok, connecting AI to real systems requires custom code,
      fragile integrations, and constant maintenance.
      Invok removes that layer completely.</span><span class="lang-es">Sin Invok, conectar la IA a sistemas reales exige código a medida,
      integraciones frágiles y mantenimiento constante.
      Invok elimina esa capa por completo.</span>
    </p>

    <div class="pillars">

      <div class="pillar">
        <div class="num">01</div>
        <h4><span class="lang-en">No integrations to build</span><span class="lang-es">Sin integraciones que construir</span></h4>
        <p><span class="lang-en">Stop writing glue code. If an API exists, your agent can use it.</span><span class="lang-es">Deja de escribir código pegamento. Si existe una API, tu agente puede usarla.</span></p>
      </div>

      <div class="pillar">
        <div class="num">02</div>
        <h4><span class="lang-en">Works with your stack</span><span class="lang-es">Encaja con tu stack</span></h4>
        <p><span class="lang-en">Use it with any agent or LLM that supports tool calling.</span><span class="lang-es">Úsalo con cualquier agente o LLM que admita llamadas a herramientas.</span></p>
      </div>

      <div class="pillar">
        <div class="num">03</div>
        <h4><span class="lang-en">Built for real execution</span><span class="lang-es">Pensado para ejecución real</span></h4>
        <p><span class="lang-en">Not just responses — real actions across real systems.</span><span class="lang-es">No solo respuestas: acciones reales en sistemas reales.</span></p>
      </div>

      <div class="pillar">
        <div class="num">04</div>
        <h4><span class="lang-en">Build once. Use everywhere.</span><span class="lang-es">Construí una vez. Usá en todos lados.</span></h4>
        <p><span class="lang-en">Once an API is converted into tools, it becomes portable. Share it with your team — or the entire community.</span><span class="lang-es">Una vez que convertís una API en herramientas, se vuelve portable. Podés compartirla con tu equipo o con toda la comunidad.</span></p>
      </div>

    </div>

  </div>
</section>


<!-- SECURITY (SIMPLIFIED) -->
<section class="security" id="security">
  <p class="section-label"><span class="lang-en">Security</span><span class="lang-es">Seguridad</span></p>
  <h2 class="section-title">
    <span class="lang-en">Your APIs stay yours.</span><span class="lang-es">Tus APIs siguen siendo tuyas.</span>
  </h2>
  <p class="section-desc">
    <span class="lang-en">Invok never exposes your credentials to the AI.
    Everything is handled securely behind the scenes.</span><span class="lang-es">Invok nunca expone tus credenciales a la IA.
    Todo se gestiona de forma segura entre bastidores.</span>
  </p>

  <ul class="security-list">

    <li>
      <span class="check">01</span>
      <div class="text">
        <h4><span class="lang-en">Credentials never reach the AI</span><span class="lang-es">Las credenciales nunca llegan a la IA</span></h4>
        <p><span class="lang-en">API keys are injected server-side only when needed.</span><span class="lang-es">Las claves se inyectan solo en el servidor y solo cuando hace falta.</span></p>
      </div>
    </li>

    <li>
      <span class="check">02</span>
      <div class="text">
        <h4><span class="lang-en">Full control</span><span class="lang-es">Control total</span></h4>
        <p><span class="lang-en">Your agent can only access what you explicitly configure.</span><span class="lang-es">Tu agente solo puede acceder a lo que configures explícitamente.</span></p>
      </div>
    </li>

    <li>
      <span class="check">03</span>
      <div class="text">
        <h4><span class="lang-en">Safe execution layer</span><span class="lang-es">Capa de ejecución segura</span></h4>
        <p><span class="lang-en">External responses are sanitized before reaching the model.</span><span class="lang-es">Las respuestas externas se sanitizan antes de llegar al modelo.</span></p>
      </div>
    </li>

  </ul>
</section>


<!-- PRICING (SIMPLE) -->
<section class="stack">
  <p class="section-label"><span class="lang-en">Pricing</span><span class="lang-es">Precios</span></p>
  <h2 class="section-title"><span class="lang-en">Simple pricing to get started</span><span class="lang-es">Precios sencillos para empezar</span></h2>

  <div class="features-grid" style="margin-top:2rem;">

    <div class="feature-card">
      <h3><span class="lang-en">Free</span><span class="lang-es">Gratis</span></h3>
      <p><span class="lang-en">• Limited APIs<br>• Basic usage<br>• Test the platform</span><span class="lang-es">• APIs limitadas<br>• Uso básico<br>• Prueba la plataforma</span></p>
    </div>

    <div class="feature-card" style="border:1px solid var(--accent);">
      <h3><span class="lang-en">Pro — $30/mo</span><span class="lang-es">Pro — 30 $/mes</span></h3>
      <p><span class="lang-en">• Unlimited APIs<br>• Higher limits<br>• Full access</span><span class="lang-es">• APIs ilimitadas<br>• Límites más altos<br>• Acceso completo</span></p>
    </div>

  </div>
</section>


<!-- CTA -->
<section class="cta">
  <h2>
    <span class="lang-en">Start building your first API pack</span><span class="lang-es">Creá tu primer pack de APIs</span>
  </h2>

  <p>
    <span class="lang-en">Or import one and start in seconds</span><span class="lang-es">O importá uno y empezá en segundos</span>
  </p>

  <div class="cta-actions">
    <a href="https://useinvok.run/" class="btn-primary" target="_blank" rel="noopener noreferrer"><span class="lang-en">Open Invok →</span><span class="lang-es">Abrir Invok →</span></a>
  </div>
</section>

  <footer>
    <div class="logo">Inv<span>ok</span></div>
    <p><span class="lang-en">Built with Spring Boot · Java 21 · MCP</span><span class="lang-es">Construido
        con Spring Boot · Java 21 · MCP</span></p>
  </footer>

  <script>

    // Language toggle
    function toggleLanguage() {
      const isEn = document.documentElement.classList.contains('lang-en');
      if (isEn) {
        document.documentElement.classList.remove('lang-en');
        document.documentElement.classList.add('lang-es');
        document.getElementById('lang-toggle').innerText = 'EN';
        document.documentElement.lang = 'es';
        document.title = 'Invok — El puente entre la IA y el mundo';
      } else {
        document.documentElement.classList.remove('lang-es');
        document.documentElement.classList.add('lang-en');
        document.getElementById('lang-toggle').innerText = 'ES';
        document.documentElement.lang = 'en';
        document.title = 'Invok — The Bridge Between AI and the World';
      }
    }

    // Cursor
    const cursor = document.getElementById('cursor');
    document.addEventListener('mousemove', e => {
      cursor.style.left = e.clientX + 'px';
      cursor.style.top = e.clientY + 'px';
    });
    document.querySelectorAll('a, button, .feature-card, .packs-step, .security-list li').forEach(el => {
      el.addEventListener('mouseenter', () => cursor.classList.add('hovering'));
      el.addEventListener('mouseleave', () => cursor.classList.remove('hovering'));
    });

    // Nav scroll
    const nav = document.getElementById('nav');
    window.addEventListener('scroll', () => {
      nav.classList.toggle('scrolled', window.scrollY > 50);
    });

    // Scroll reveal
    const observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = '1';
          entry.target.style.transform = 'translateY(0)';
        }
      });
    }, { threshold: 0.1 });

    document.querySelectorAll('.feature-card, .packs-step, .pillar, .security-list li, .tech-pill').forEach(el => {
      el.style.opacity = '0';
      el.style.transform = 'translateY(16px)';
      el.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
      observer.observe(el);
    });
  </script>
</body>

</html>