import React from "react";
import { AbsoluteFill, staticFile } from "remotion";
import { BrowserFrame, Bullet, COLORS, SectionHeading } from "./shared";

export const FeatureScene: React.FC = () => {
  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1000px 600px at 85% 90%, rgba(79,140,255,0.10), transparent 60%)`,
        padding: "96px 90px",
      }}
    >
      <SectionHeading kicker="Feature Management" title="特性管理" />

      <div style={{ display: "flex", gap: 64, marginTop: 56, alignItems: "center" }}>
        <div style={{ display: "flex", flexDirection: "column", gap: 44 }}>
          <Bullet
            index={1}
            title="特性即功能开关"
            desc="将被测功能点建模为 Feature，配置项控制其表现行为"
            delay={14}
          />
          <Bullet
            index={2}
            title="模版化配置"
            desc="基于模板定义配置结构，支持多种数据类型与校验"
            delay={24}
          />
          <Bullet
            index={3}
            title="启用 / 禁用"
            desc="一键上下线，变更实时推送至 C 端"
            delay={34}
          />
        </div>

        <div style={{ marginLeft: "auto" }}>
          <BrowserFrame
            src={staticFile("screenshots/feature-list.png")}
            width={1230}
            url="localhost:3333/#/feature/list"
          />
        </div>
      </div>
    </AbsoluteFill>
  );
};
