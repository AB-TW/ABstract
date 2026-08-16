import React from "react";
import { AbsoluteFill, staticFile } from "remotion";
import { BrowserFrame, Bullet, COLORS, SectionHeading } from "./shared";

export const ConfigScene: React.FC = () => {
  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1000px 600px at 12% 88%, rgba(139,92,246,0.10), transparent 60%)`,
        padding: "96px 90px",
      }}
    >
      <SectionHeading kicker="Config Versions" title="配置管理" />

      <div style={{ display: "flex", gap: 64, marginTop: 56, alignItems: "center" }}>
        <BrowserFrame
          src={staticFile("screenshots/feature-configs.png")}
          width={1230}
          url="localhost:3333/#/feature/config/list"
        />

        <div style={{ display: "flex", flexDirection: "column", gap: 44, marginLeft: "auto" }}>
          <Bullet index={1} title="多版本配置" desc="同一特性下维护多套配置，草稿与已发布并存" delay={14} />
          <Bullet index={2} title="优先级排序" desc="按 priority 决定命中顺序，支持置顶" delay={24} />
          <Bullet
            index={3}
            title="发布 / 禁用"
            desc="DRAFT → PUBLISHED / DISABLED，到期自动失效"
            delay={34}
          />
        </div>
      </div>
    </AbsoluteFill>
  );
};
