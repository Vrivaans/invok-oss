package io.invok.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tool_parameters")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ToolParameter extends BaseModel {

    private String name;

    @Enumerated(EnumType.STRING)
    private ParameterType type;

    private String description;
    private Boolean required;
    private String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_tool_id")
    private ApiTool apiTool;
}
