import React from "react";
import { AbsoluteFill, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { BrowserFrame, COLORS, FONT, SectionHeading } from "./shared";

const STAGES = [
  { label: "DRAFT", color: COLORS.muted },
  { label: "PUBLISHED", color: COLORS.green },
  { label: "PAUSED", color: COLORS.amber },
  { label: "STOPPED", color: COLORS.red },
];

export const ExperimentScene: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1000px 600px at 50% 110%, rgba(52,211,153,0.08), transparent 60%)`,
        padding: "88px 90px",
      }}
    >
      <SectionHeading kicker="Experiment Lifecycle" title="实验管理" />

      <div style={{ display: "flex", gap: 40, marginTop: 44, alignItems: "flex-start" }}>
        <BrowserFrame
          src={staticFile("screenshots/experiment-list.png")}
          width={1010}
          url="localhost:3333/#/experiment/list"
          delay={10}
        />
        <div style={{ position: "relative", flex: 1 }}>
          <div style={{ translate: "0 90px" }}>
            <BrowserFrame
              src={staticFile("screenshots/experiment-edit.png")}
              width={620}
              url="localhost:3333/#/experiment/edit/1"
              delay={22}
            />
          </div>
        </div>
      </div>

      {/* lifecycle strip */}
      <div
        style={{
          position: "absolute",
          left: 90,
          right: 90,
          bottom: 86,
          display: "flex",
          alignItems: "center",
          gap: 26,
        }}
      >
        {STAGES.map((s, i) => {
          const p = spring({ frame: frame - 42 - i * 7, fps, config: { damping: 200, stiffness: 120 } });
          return (
            <div key={s.label} style={{ display: "flex", alignItems: "center", gap: 26 }}>
              {i > 0 && (
                <div
                  style={{
                    fontSize: 44,
                    color: COLORS.muted,
                    opacity: interpolate(frame, [42 + i * 7, 50 + i * 7], [0, 1], {
                      extrapolateLeft: "clamp",
                      extrapolateRight: "clamp",
                    }),
                  }}
                >
                  →
                </div>
              )}
              <div
                style={{
                  padding: "16px 36px",
                  borderRadius: 16,
                  border: `2px solid ${s.color}`,
                  backgroundColor: "rgba(255,255,255,0.04)",
                  fontFamily: MONO_LABEL_FONT,
                  fontSize: 32,
                  fontWeight: 700,
                  color: s.color,
                  opacity: interpolate(p, [0, 1], [0, 1]),
                  scale: interpolate(p, [0, 1], [0.7, 1]),
                }}
              >
                {s.label}
              </div>
            </div>
          );
        })}
        <div
          style={{
            marginLeft: "auto",
            fontFamily: FONT,
            fontSize: 30,
            color: COLORS.muted,
            opacity: interpolate(frame, [fps * 2.4, fps * 3], [0, 1], {
              extrapolateLeft: "clamp",
              extrapolateRight: "clamp",
            }),
          }}
        >
          分组比例 · 桶流量分配 · 分桶结果统计
        </div>
      </div>
    </AbsoluteFill>
  );
};

const MONO_LABEL_FONT = '"SF Mono", "JetBrains Mono", Menlo, Consolas, monospace';
