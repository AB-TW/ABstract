import React from "react";
import { AbsoluteFill, interpolate, spring, useCurrentFrame, useVideoConfig } from "remotion";
import { COLORS, FONT, MONO, SectionHeading } from "./shared";

const LayerBox: React.FC<{
  title: string;
  subtitle: string;
  width?: number;
  color: string;
  delay: number;
}> = ({ title, subtitle, width = 560, color, delay }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const p = spring({ frame: frame - delay, fps, config: { damping: 200, stiffness: 100 } });

  return (
    <div
      style={{
        width,
        padding: "22px 36px",
        borderRadius: 18,
        border: `2px solid ${color}`,
        backgroundColor: "rgba(255,255,255,0.045)",
        textAlign: "center",
        opacity: interpolate(p, [0, 1], [0, 1]),
        translate: `0px ${interpolate(p, [0, 1], [36, 0])}px`,
      }}
    >
      <div style={{ fontFamily: MONO, fontSize: 34, fontWeight: 700, color }}>{title}</div>
      <div style={{ fontFamily: FONT, fontSize: 26, color: COLORS.muted, marginTop: 6 }}>{subtitle}</div>
    </div>
  );
};

const Arrow: React.FC<{ delay: number }> = ({ delay }) => {
  const frame = useCurrentFrame();
  return (
    <div
      style={{
        fontSize: 40,
        color: COLORS.accent,
        lineHeight: 1.1,
        opacity: interpolate(frame, [delay, delay + 8], [0, 1], {
          extrapolateLeft: "clamp",
          extrapolateRight: "clamp",
        }),
      }}
    >
      ▼
    </div>
  );
};

export const ArchitectureScene: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1000px 560px at 50% -10%, rgba(79,140,255,0.10), transparent 60%)`,
        padding: "84px 90px",
        alignItems: "center",
      }}
    >
      <SectionHeading kicker="Architecture" title="DDD 分层架构" />

      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 12,
          marginTop: 40,
        }}
      >
        <LayerBox title="frontend" subtitle="Vue 3 + TypeScript 管理界面" color="#38bdf8" delay={10} width={620} />
        <Arrow delay={20} />
        <div style={{ display: "flex", gap: 36 }}>
          <LayerBox title="api · REST :8080" subtitle="管理端接口（薄层）" color={COLORS.accent} delay={26} />
          <LayerBox title="api · RSocket :7001" subtitle="C 端统一接口" color={COLORS.accent} delay={32} />
        </div>
        <Arrow delay={40} />
        <LayerBox
          title="domain"
          subtitle="纯领域模型 · 零框架依赖 · 接口驱动"
          color={COLORS.accent2}
          delay={46}
          width={760}
        />
        <Arrow delay={56} />
        <div style={{ display: "flex", gap: 36 }}>
          <LayerBox title="persistent-couchbase" subtitle="Couchbase 实现（默认）" color={COLORS.green} delay={62} />
          <LayerBox title="persistent-r2dbc" subtitle="PostgreSQL R2DBC 实现" color={COLORS.green} delay={68} />
        </div>
      </div>

      <div
        style={{
          position: "absolute",
          right: 90,
          bottom: 96,
          display: "flex",
          flexDirection: "column",
          gap: 14,
          alignItems: "flex-end",
        }}
      >
        {["全响应式 Mono / Flux", "聚合根模式", "持久化可插拔"].map((t, i) => (
          <div
            key={t}
            style={{
              fontFamily: FONT,
              fontSize: 28,
              color: COLORS.muted,
              opacity: interpolate(frame, [fps * 2.6 + i * 8, fps * 3.1 + i * 8], [0, 1], {
                extrapolateLeft: "clamp",
                extrapolateRight: "clamp",
              }),
            }}
          >
            ✦ {t}
          </div>
        ))}
      </div>
    </AbsoluteFill>
  );
};
