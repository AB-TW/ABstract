import React from "react";
import { AbsoluteFill, Img, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig } from "remotion";
import { Badge, COLORS, FONT } from "./shared";

export const OutroScene: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const p = spring({ frame, fps, config: { damping: 200, stiffness: 80 } });

  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1100px 620px at 50% 40%, rgba(79,140,255,0.14), transparent 62%)`,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: 40,
      }}
    >
      <Img
        src={staticFile("ABtractLogo.png")}
        style={{
          width: 560,
          height: "auto",
          opacity: interpolate(p, [0, 1], [0, 1]),
          scale: interpolate(p, [0, 1], [0.6, 1]),
        }}
      />
      <div
        style={{
          fontFamily: FONT,
          fontSize: 42,
          color: COLORS.muted,
          opacity: interpolate(frame, [fps * 0.7, fps * 1.2], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" }),
        }}
      >
        让每一个功能点，都值得被实验
      </div>
      <div
        style={{
          display: "flex",
          gap: 18,
          marginTop: 16,
        }}
      >
        {["Java 21", "Spring Boot 4", "Reactive", "DDD"].map((b, i) => (
          <div
            key={b}
            style={{
              opacity: interpolate(frame, [fps * 1.2 + i * 5, fps * 1.7 + i * 5], [0, 1], {
                extrapolateLeft: "clamp",
                extrapolateRight: "clamp",
              }),
            }}
          >
            <Badge label={b} />
          </div>
        ))}
      </div>
    </AbsoluteFill>
  );
};
