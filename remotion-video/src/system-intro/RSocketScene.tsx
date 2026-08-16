import React from "react";
import { AbsoluteFill, interpolate, spring, useCurrentFrame, useVideoConfig } from "remotion";
import { Bullet, COLORS, FONT, MONO, SectionHeading } from "./shared";

const CmdLine: React.FC<{ children: React.ReactNode; color?: string }> = ({ children, color = COLORS.text }) => (
  <div style={{ fontFamily: MONO, fontSize: 27, lineHeight: 1.6, color, whiteSpace: "pre" }}>
    {children}
  </div>
);

export const RSocketScene: React.FC = () => {
  const frame = useCurrentFrame();
  const { fps } = useVideoConfig();
  const cardProgress = spring({ frame: frame - 8, fps, config: { damping: 200, stiffness: 90 } });

  return (
    <AbsoluteFill
      style={{
        backgroundColor: COLORS.bg,
        backgroundImage: `radial-gradient(1000px 600px at 15% 10%, rgba(79,140,255,0.10), transparent 60%)`,
        padding: "88px 90px",
      }}
    >
      <SectionHeading kicker="Client Interface" title="C 端统一接口" />

      <div style={{ display: "flex", gap: 56, marginTop: 50, alignItems: "flex-start" }}>
        {/* terminal card */}
        <div
          style={{
            width: 1030,
            borderRadius: 20,
            backgroundColor: "#0b1322",
            border: "1px solid rgba(255,255,255,0.10)",
            boxShadow: "0 30px 70px rgba(0,0,0,0.5)",
            padding: "30px 36px",
            opacity: interpolate(cardProgress, [0, 1], [0, 1]),
            translate: `0px ${interpolate(cardProgress, [0, 1], [50, 0])}px`,
          }}
        >
          <CmdLine color={COLORS.green}>$ rsc --route=customers.featureConfigs.{"{featureKey}"} --stream \</CmdLine>
          <CmdLine color={COLORS.green}>{`    -m '{"customerId":"111","clientId":"610101"}' \\`}</CmdLine>
          <CmdLine color={COLORS.green}>    --mmt application/json tcp://localhost:7001</CmdLine>

          <div style={{ height: 22 }} />

          <div style={{ opacity: interpolate(frame, [fps * 1.3, fps * 1.8], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" }) }}>
            <CmdLine color="#7dd3fc">{"{ \"key\": \"onboarding-v2\",  \"data\": { \"steps\": 5, \"reward\": \"points\" } }"}</CmdLine>
          </div>
          <div style={{ height: 10 }} />
          <div style={{ opacity: interpolate(frame, [fps * 1.9, fps * 2.4], [0, 1], { extrapolateLeft: "clamp", extrapolateRight: "clamp" }) }}>
            <CmdLine color="#c4b5fd">{"{ \"key\": \"onboarding-v3\",  \"data\": { \"steps\": 3, \"reward\": \"coupon\" } }"}</CmdLine>
          </div>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: 40, marginLeft: "auto" }}>
          <Bullet index={1} title="单一入口" desc="RSocket 流式拉取命中的配置" delay={16} />
          <Bullet index={2} title="实验无感" desc="实验分组与固定配置统一返回，C 端零改动" delay={26} />
          <Bullet index={3} title="实时推送" desc="配置变更经 WebSocket 即时下发" delay={36} />
        </div>
      </div>
    </AbsoluteFill>
  );
};
