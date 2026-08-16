import React from "react";
import { Img, interpolate, spring, useCurrentFrame, useVideoConfig } from "remotion";

export const COLORS = {
  bg: "#070d1a",
  panel: "rgba(255,255,255,0.045)",
  panelBorder: "rgba(255,255,255,0.10)",
  accent: "#4f8cff",
  accent2: "#8b5cf6",
  green: "#34d399",
  amber: "#fbbf24",
  red: "#f87171",
  text: "#f1f5f9",
  muted: "#94a3b8",
};

export const FONT =
  '-apple-system, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif';
export const MONO = '"SF Mono", "JetBrains Mono", Menlo, Consolas, monospace';

/** Fake browser chrome wrapper for UI screenshots */
export const BrowserFrame: React.FC<{
  src: string;
  width: number;
  url?: string;
  delay?: number;
  style?: React.CSSProperties;
}> = ({ src, width, url = "localhost:3333", delay = 8, style }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const height = Math.round((width * 1800) / 3200);

  const progress = spring({ frame: frame - delay, fps, config: { damping: 200, stiffness: 90 } });

  return (
    <div
      style={{
        width,
        borderRadius: 18,
        overflow: "hidden",
        backgroundColor: "#0f172a",
        boxShadow: "0 40px 90px rgba(0,0,0,0.55), 0 0 0 1px rgba(255,255,255,0.08)",
        opacity: interpolate(progress, [0, 1], [0, 1]),
        translate: `0px ${interpolate(progress, [0, 1], [60, 0])}px`,
        scale: interpolate(progress, [0, 1], [0.92, 1]),
        ...style,
      }}
    >
      <div
        style={{
          height: 44,
          backgroundColor: "#111a2e",
          display: "flex",
          alignItems: "center",
          paddingLeft: 20,
          gap: 9,
          borderBottom: "1px solid rgba(255,255,255,0.07)",
        }}
      >
        <div style={{ width: 13, height: 13, borderRadius: 99, backgroundColor: "#ff5f57" }} />
        <div style={{ width: 13, height: 13, borderRadius: 99, backgroundColor: "#febc2e" }} />
        <div style={{ width: 13, height: 13, borderRadius: 99, backgroundColor: "#28c840" }} />
        <div
          style={{
            marginLeft: 18,
            height: 26,
            flex: 1,
            maxWidth: width * 0.55,
            borderRadius: 99,
            backgroundColor: "rgba(255,255,255,0.07)",
            display: "flex",
            alignItems: "center",
            paddingLeft: 14,
            color: COLORS.muted,
            fontFamily: MONO,
            fontSize: 15,
          }}
        >
          {url}
        </div>
      </div>
      <Img src={src} style={{ width, height, display: "block" }} />
    </div>
  );
};

/** Section heading with kicker */
export const SectionHeading: React.FC<{ kicker: string; title: string }> = ({ kicker, title }) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const progress = spring({ frame, fps, config: { damping: 200, stiffness: 110 } });

  return (
    <div
      style={{
        opacity: interpolate(progress, [0, 1], [0, 1]),
        translate: `0px ${interpolate(progress, [0, 1], [-40, 0])}px`,
      }}
    >
      <div
        style={{
          fontFamily: FONT,
          fontSize: 30,
          fontWeight: 700,
          letterSpacing: 7,
          color: COLORS.accent,
          textTransform: "uppercase",
        }}
      >
        {kicker}
      </div>
      <div
        style={{
          fontFamily: FONT,
          fontSize: 76,
          fontWeight: 800,
          color: COLORS.text,
          marginTop: 8,
        }}
      >
        {title}
      </div>
      <div
        style={{
          width: 110,
          height: 8,
          borderRadius: 99,
          marginTop: 18,
          backgroundImage: `linear-gradient(90deg, ${COLORS.accent}, ${COLORS.accent2})`,
        }}
      />
    </div>
  );
};

/** Bullet item with number chip */
export const Bullet: React.FC<{ index: number; title: string; desc: string; delay?: number }> = ({
  index,
  title,
  desc,
  delay = 0,
}) => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const progress = spring({ frame: frame - delay, fps, config: { damping: 200, stiffness: 100 } });

  return (
    <div
      style={{
        display: "flex",
        gap: 22,
        alignItems: "flex-start",
        opacity: interpolate(progress, [0, 1], [0, 1]),
        translate: `${interpolate(progress, [0, 1], [-50, 0])}px 0px`,
      }}
    >
      <div
        style={{
          minWidth: 52,
          height: 52,
          borderRadius: 14,
          backgroundImage: `linear-gradient(135deg, ${COLORS.accent}, ${COLORS.accent2})`,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontFamily: FONT,
          fontSize: 26,
          fontWeight: 800,
          color: "white",
          marginTop: 4,
        }}
      >
        {index}
      </div>
      <div>
        <div style={{ fontFamily: FONT, fontSize: 42, fontWeight: 700, color: COLORS.text }}>
          {title}
        </div>
        <div
          style={{
            fontFamily: FONT,
            fontSize: 29,
            color: COLORS.muted,
            lineHeight: 1.45,
            marginTop: 6,
          }}
        >
          {desc}
        </div>
      </div>
    </div>
  );
};

/** Small pill badge */
export const Badge: React.FC<{ label: string; color?: string }> = ({
  label,
  color = COLORS.text,
}) => (
  <div
    style={{
      padding: "14px 30px",
      borderRadius: 99,
      border: `1.5px solid ${COLORS.panelBorder}`,
      backgroundColor: COLORS.panel,
      fontFamily: MONO,
      fontSize: 27,
      color,
    }}
  >
    {label}
  </div>
);
