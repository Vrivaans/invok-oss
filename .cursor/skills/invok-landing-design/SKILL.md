---
name: invok-landing-design
description: >-
  Aplica la guía visual de la landing Invok (tokens CSS, tipografía, secciones,
  componentes, i18n y motion). Usa al trabajar en el frontend de la landing,
  al refactorizar UI, al ajustar estilos o cuando el usuario mencione diseño,
  marca, hero, tokens, o la carpeta .design.
---

# Diseño landing Invok

## Fuente de verdad

Las instrucciones completas, checklist y detalle de componentes están en el repo (rutas desde la raíz del proyecto):

- **`.design/design.md`** — leer este archivo al aplicar o revisar diseño de la landing.
- **`.design/img-reference.jpeg`** — referencia visual; abrir cuando haga falta comparar tono y jerarquía con la captura.

## Qué hacer

1. Antes de cambiar UI de la landing, **abrir y seguir** `.design/design.md` (tokens, tipografía, orden de secciones, rejillas, i18n).
2. Si hay duda sobre aspecto general, **consultar** `img-reference.jpeg`.
3. Si se modifica el comportamiento o la estructura documentada, **actualizar** `design.md` en el mismo cambio cuando corresponda (el documento lo indica al final).

## Resumen mínimo (no sustituye al documento)

- Fondo oscuro, acento lima `#c8f04a`, secundario menta `#4af0c8`; variables en `:root` según la tabla de `design.md`.
- Fuentes: DM Serif Display, DM Mono, Instrument Sans — jerarquía `.section-label` / `.section-title` / `.section-desc`.
- Orden de secciones y `id` de anclas alineados con el nav (`#how`, `#features`, `#philosophy`, `#security`).

Para el resto (hero, grids, filosofía, seguridad, CTA, motion, responsive), usar **solo** el contenido de `.design/design.md`.
