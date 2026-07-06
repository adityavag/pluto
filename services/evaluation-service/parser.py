from typing import Any


def parse_excalidraw(excalidraw_json: dict[str, Any]) -> str:
    """Convert an Excalidraw JSON export into a concise, semantic
    description of components and connections, stripping visual noise.
    """
    elements: list[dict[str, Any]] = excalidraw_json.get("elements", [])
    if not elements:
        return "(Empty diagram — no elements found)"

    element_map: dict[str, dict[str, Any]] = {}
    text_elements: list[dict[str, Any]] = []
    arrows: list[dict[str, Any]] = []
    containers: list[dict[str, Any]] = []

    # Filter and group elements
    for el in elements:
        if el.get("isDeleted"):
            continue

        el_id = el.get("id", "")
        el_type = el.get("type", "")
        element_map[el_id] = el

        if el_type == "text":
            text_elements.append(
                {
                    "id": el_id,
                    "text": (el.get("text") or "").strip(),
                    "containerId": el.get("containerId"),
                }
            )
        elif el_type == "arrow":
            start_binding = el.get("startBinding") or {}
            end_binding = el.get("endBinding") or {}
            arrows.append(
                {
                    "id": el_id,
                    "start": start_binding.get("elementId"),
                    "end": end_binding.get("elementId"),
                    "label": None,
                }
            )
        elif el_type in ("rectangle", "ellipse", "diamond", "image"):
            containers.append(
                {
                    "id": el_id,
                    "type": el_type,
                    "boundElements": el.get("boundElements") or [],
                }
            )

    # Map text labels to container shapes
    container_labels: dict[str, str] = {}
    for text_el in text_elements:
        container_id = text_el.get("containerId")
        if container_id and text_el["text"]:
            container_labels[container_id] = text_el["text"]

    # Map text labels to arrows
    for arrow in arrows:
        arrow_el = element_map.get(arrow["id"], {})
        bound_els = arrow_el.get("boundElements") or []
        for bound in bound_els:
            if bound.get("type") == "text":
                bound_text_id = bound.get("id", "")
                for text_el in text_elements:
                    if text_el["id"] == bound_text_id and text_el["text"]:
                        arrow["label"] = text_el["text"]
                        break

    # Build textual description
    lines: list[str] = []

    # 1. Components
    component_lines: list[str] = []
    for container in containers:
        label = container_labels.get(container["id"])
        if label:
            type_tag = container["type"].upper()
            component_lines.append(f"  - [{type_tag}] {label}")

    if component_lines:
        lines.append("Components:")
        lines.extend(component_lines)

    # 2. Free-floating annotations
    note_lines: list[str] = []
    for text_el in text_elements:
        if not text_el.get("containerId") and text_el["text"]:
            is_arrow_label = any(
                a["label"] == text_el["text"] for a in arrows if a["label"]
            )
            if not is_arrow_label:
                note_lines.append(f"  - {text_el['text']}")

    if note_lines:
        if lines:
            lines.append("")
        lines.append("Notes:")
        lines.extend(note_lines)

    # 3. Connections
    connection_lines: list[str] = []
    for arrow in arrows:
        start_label = _resolve_label(arrow["start"], container_labels, element_map)
        end_label = _resolve_label(arrow["end"], container_labels, element_map)
        label_suffix = f"  [{arrow['label']}]" if arrow["label"] else ""
        connection_lines.append(f"  - {start_label} → {end_label}{label_suffix}")

    if connection_lines:
        if lines:
            lines.append("")
        lines.append("Connections:")
        lines.extend(connection_lines)

    if not lines:
        return "(Diagram has elements but no readable labels or connections)"

    return "\n".join(lines)


def _resolve_label(
    element_id: str | None,
    container_labels: dict[str, str],
    element_map: dict[str, dict[str, Any]],
) -> str:
    if not element_id:
        return "(?)"
    if element_id in container_labels:
        return container_labels[element_id]
    el = element_map.get(element_id, {})
    text = (el.get("text") or "").strip()
    if text:
        return text
    return f"(unnamed {el.get('type', 'element')})"
