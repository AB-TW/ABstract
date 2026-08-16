import React from "react";
import { AbsoluteFill, Img, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Badge, COLORS, FONT } from "./shared";

export const TitleScene: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();

  const logoProgress = spring({ frame, fps, config: { damping: 200, stiffness: 80 } });
  const subProgress = spring({ frame: frame - 20, fps, config: { damping: 200, stiffness: 90 } });
  const badges = ["Java 21", "Spring Boot 4", "WebFlux · RSocket", "Couchbase + R2DBC", "Vue 3"];

  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1100px 620px at 78% 12%, rgba(79,140,255,0.16), transparent 60%),
                          radial-gradient(900px 560px at 18% 88%, rgba(139,92,246,0.14), transparent 60%)`,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 34 }}>
        <Img
          src={staticFile("ABtractLogo.png")}
          style={{
            width: 680,
            height: "auto",
            opacity: interpolate(logoProgress, [0, 1], [0, 1]),
            scale: interpolate(logoProgress, [0, 1], [0.6, 1]),
          }}
        />
        <div
          style={{
            fontFamily: FONT,
            fontSize: 46,
            fontWeight: 600,
            backgroundImage: `linear-gradient(90deg, ${COLORS.accent}, ${COLORS.accent2})`,
            WebkitBackgroundClip: "text",
            backgroundClip: "text",
            color: "transparent",
            opacity: interpolate(subProgress, [0, 1], [0, 1]),
            translate: `0px ${interpolate(subProgress, [0, 1], [30, 0])}px`,
          }}
        >
          AB 测试脚手架平台 · 特性开关 × 实验管理
        </div>
      </div>

      <div
        style={{
          position: "absolute",
          bottom: 110,
          display: "flex",
          gap: 20,
          opacity: interpolate(frame, [fps * 1.4, fps * 2.1], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" }),
        }}
      >
        {badges.map((b, i) => (
          <div
            key={b}
            style={{
              opacity: interpolate(frame, [fps * 1.4 + i * 4, fps * 1.9 + i * 4], [0, 1], {
                extrapolateLeft: "clamp",
                extrapolateRight: "clamp",
              }),
              translate: `0px ${interpolate(frame, [fps * 1.4 + i * 4, fps * 1.9 + i * 4], [16, 0], {
                extrapolateLeft: "clamp",
                extrapolateRight: "clamp",
              })}px`,
            }}
          >
            <Badge label={b} />
          </div>
        ))}
      </div>
    </AbsoluteFill>
  );
};
