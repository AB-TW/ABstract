import {
  AbsoluteFill,
  interpolate,
  spring,
  useCurrentFrame,
  useVideoConfig,
  Sequence,
  Audio,
} from "remotion";
import backgroundMusic from "../public/audio/mixkit-tech-house-vibes-130.mp3";
import { FC } from "react";

interface SlideProps {
  title: string;
  subtitle: string;
  items: string[];
  frame: number;
  delay: number;
}

const Slide: FC<SlideProps> = ({ title, subtitle, items, frame, delay }) => {
  const { fps } = useVideoConfig();
  const opacity = spring({
    frame: frame - delay,
    fps,
    config: { damping: 200, stiffness: 100 },
  });

  const scale = spring({
    frame: frame - delay,
    fps,
    config: { damping: 200, stiffness: 100 },
  });

  return (
    <div
      style={{
        opacity,
        transform: `scale(${0.8 + scale * 0.2})`,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        height: "100%",
        padding: 80,
      }}
    >
      <h1
        style={{
          fontSize: 96,
          fontWeight: "bold",
          color: "#60A5FA",
          marginBottom: 24,
          textShadow: "0 4px 20px rgba(96, 165, 250, 0.5)",
        }}
      >
        {title}
      </h1>
      <p
        style={{
          fontSize: 42,
          color: "#E5E7EB",
          marginBottom: 60,
          textAlign: "center",
        }}
      >
        {subtitle}
      </p>
      <ul
        style={{
          fontSize: 32,
          color: "#D1D5DB",
          listStyle: "none",
          padding: 0,
          textAlign: "left",
        }}
      >
        {items.map((item, i) => (
          <li
            key={i}
            style={{
              marginBottom: 20,
              opacity: Math.max(
                0,
                spring({
                  frame: frame - delay - 15 - i * 5,
                  fps,
                  config: { damping: 200, stiffness: 80 },
                })
              ),
              transform: `translateX(${50 * (1 - spring({
                frame: frame - delay - 15 - i * 5,
                fps,
                config: { damping: 200, stiffness: 80 },
              }))}px)`,
            }}
          >
            • {item}
          </li>
        ))}
      </ul>
    </div>
  );
};

const TechBadge: FC<{
  name: string;
  color: string;
  frame: number;
  delay: number;
}> = ({ name, color, frame, delay }) => {
  const { fps } = useVideoConfig();
  const opacity = spring({
    frame: frame - delay,
    fps,
    config: { damping: 200, stiffness: 100 },
  });

  const y = interpolate(
    spring({
      frame: frame - delay,
      fps,
      config: { damping: 200, stiffness: 100 },
    }),
    [0, 1],
    [50, 0]
  );

  return (
    <div
      style={{
        opacity,
        transform: `translateY(${y}px)`,
        backgroundColor: color,
        padding: "16px 32px",
        borderRadius: 12,
        fontSize: 28,
        fontWeight: 600,
        color: "white",
        boxShadow: `0 4px 20px ${color}80`,
      }}
    >
      {name}
    </div>
  );
};

const ArchitectureDiagram: FC<{ frame: number }> = ({ frame }) => {
  const { fps } = useVideoConfig();
  const progress = spring({
    frame: frame - 30,
    fps,
    config: { damping: 200, stiffness: 60 },
  });

  const layers = [
    { name: "API Layer", color: "#F59E0B" },
    { name: "Domain Layer", color: "#10B981" },
    { name: "Persistent Layer", color: "#8B5CF6" },
  ];

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 20, marginTop: 40 }}>
      {layers.map((layer, i) => {
        const layerProgress = Math.max(0, Math.min(1, (progress * 3) - i));
        const width = interpolate(layerProgress, [0, 1], [0, 500]);
        const opacity = spring({
          frame: frame - 30 - i * 10,
          fps,
          config: { damping: 200, stiffness: 80 },
        });

        return (
          <div key={layer.name} style={{ position: "relative", width: 500, height: 70 }}>
            <div
              style={{
                width,
                height: 70,
                backgroundColor: layer.color,
                borderRadius: 12,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: 26,
                fontWeight: 600,
                color: "white",
                opacity,
                boxShadow: `0 4px 20px ${layer.color}80`,
              }}
            >
              {layer.name}
            </div>
            {i > 0 && (
              <div
                style={{
                  position: "absolute",
                  top: -35,
                  left: "50%",
                  transform: "translateX(-50%)",
                  fontSize: 32,
                  opacity: opacity * 0.5,
                  color: "#D1D5DB",
                }}
              >
                ↓
              </div>
            )}
          </div>
        );
      })}
    </div>
  );
};

// Frontend showcase component - displays app interface mockups
const FrontendShowcase: FC<{ frame: number }> = ({ frame }) => {
  const { fps } = useVideoConfig();
  const opacity = spring({
    frame: frame - 30,
    fps,
    config: { damping: 200, stiffness: 100 },
  });

  const scale = spring({
    frame: frame - 30,
    fps,
    config: { damping: 200, stiffness: 80 },
  });

  const slideIn = interpolate(scale, [0, 1], [100, 0]);

  // Mock UI cards representing different pages
  const pages = [
    { name: "Feature Management", icon: "⚙️", color: "#3B82F6", description: "Manage feature flags and configurations" },
    { name: "Experiment List", icon: "🔬", color: "#10B981", description: "Create and monitor AB tests" },
    { name: "Filter Templates", icon: "🎯", color: "#F59E0B", description: "Define audience segments" },
    { name: "Dashboard", icon: "📊", color: "#8B5CF6", description: "Real-time analytics overview" },
  ];

  return (
    <div
      style={{
        opacity,
        transform: `translateX(${slideIn}px)`,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        height: "100%",
        padding: 60,
      }}
    >
      <h2
        style={{
          fontSize: 64,
          color: "#60A5FA",
          marginBottom: 16,
        }}
      >
        Web Interface
      </h2>
      <p style={{ fontSize: 28, color: "#D1D5DB", marginBottom: 50 }}>
        Intuitive management console for all your AB testing needs
      </p>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(2, 1fr)",
          gap: 40,
          maxWidth: 1200,
        }}
      >
        {pages.map((page, i) => {
          const cardOpacity = spring({
            frame: frame - 45 - i * 8,
            fps,
            config: { damping: 200, stiffness: 80 },
          });
          const cardScale = spring({
            frame: frame - 45 - i * 8,
            fps,
            config: { damping: 200, stiffness: 80 },
          });

          return (
            <div
              key={page.name}
              style={{
                opacity: cardOpacity,
                transform: `scale(${0.8 + cardScale * 0.2})`,
                background: `linear-gradient(135deg, ${page.color}20, ${page.color}40)`,
                border: `2px solid ${page.color}60`,
                borderRadius: 20,
                padding: 40,
                boxShadow: `0 8px 32px ${page.color}40`,
              }}
            >
              <div style={{ fontSize: 64, marginBottom: 20 }}>{page.icon}</div>
              <h3 style={{ fontSize: 32, color: "#F3F4F6", marginBottom: 12 }}>
                {page.name}
              </h3>
              <p style={{ fontSize: 20, color: "#D1D5DB", lineHeight: 1.5 }}>
                {page.description}
              </p>
            </div>
          );
        })}
      </div>

      <div
        style={{
          marginTop: 50,
          padding: "20px 40px",
          background: "rgba(96, 165, 250, 0.2)",
          border: "2px solid #60A5FA80",
          borderRadius: 16,
          fontSize: 24,
          color: "#E5E7EB",
        }}
      >
        🎨 Modern Vue 3 + Element Plus UI
      </div>
    </div>
  );
};

export const ABtractIntro: FC = () => {
  const frame = useCurrentFrame();
  const { fps, durationInFrames } = useVideoConfig();

  const bgGradient = `linear-gradient(135deg, #0F172A 0%, #1E293B 50%, #334155 100%)`;

  return (
    <AbsoluteFill style={{ background: bgGradient, fontFamily: "system-ui, -apple-system, sans-serif" }}>
      {/* Background Music - tech house vibes */}
      <Audio
        src={backgroundMusic}
        volume={0.3}
      />
      {/* Title Slide - 0 to 180 frames */}
      <Sequence from={0} durationInFrames={180}>
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            height: "100%",
          }}
        >
          <div
            style={{
              fontSize: 180,
              fontWeight: 900,
              background: "linear-gradient(135deg, #60A5FA, #A78BFA, #F472B6)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
              marginBottom: 40,
              opacity: spring({
                frame,
                fps,
                config: { damping: 200, stiffness: 60 },
              }),
              transform: `scale(${0.5 + spring({ frame, fps, config: { damping: 200, stiffness: 60 } }) * 0.5})`,
            }}
          >
            ABtract
          </div>
          <p
            style={{
              fontSize: 48,
              color: "#E5E7EB",
              opacity: spring({
                frame: frame - 15,
                fps,
                config: { damping: 200, stiffness: 80 },
              }),
            }}
          >
            AB Testing Scaffold Platform
          </p>
        </div>
      </Sequence>

      {/* Purpose Slide - 180 to 360 frames */}
      <Sequence from={180} durationInFrames={180}>
        <Slide
          title="What is ABtract?"
          subtitle="A powerful platform for AB testing and feature management"
          items={[
            "Feature Management with template-based configuration",
            "Experiment Lifecycle Management with group ratios",
            "Unified C-end Interfaces for seamless experience",
            "Real-time configuration updates via RSocket"
          ]}
          frame={frame}
          delay={180}
        />
      </Sequence>

      {/* Tech Stack - Backend - 360 to 540 frames */}
      <Sequence from={360} durationInFrames={180}>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", height: "100%", padding: 80 }}>
          <h2 style={{ fontSize: 72, color: "#60A5FA", marginBottom: 60, opacity: spring({ frame: frame - 360, fps, config: { damping: 200, stiffness: 100 } }) }}>
            Backend Tech Stack
          </h2>
          <div style={{ display: "flex", gap: 24, flexWrap: "wrap", justifyContent: "center", maxWidth: 1400 }}>
            <TechBadge name="Java 21" color="#EF4444" frame={frame} delay={375} />
            <TechBadge name="Spring Boot 4.0" color="#6DB33F" frame={frame} delay={385} />
            <TechBadge name="WebFlux + RSocket" color="#22C55E" frame={frame} delay={395} />
            <TechBadge name="Couchbase / PostgreSQL" color="#DC2626" frame={frame} delay={405} />
            <TechBadge name="R2DBC Reactive" color="#3B82F6" frame={frame} delay={415} />
            <TechBadge name="AI Integration" color="#8B5CF6" frame={frame} delay={425} />
          </div>
        </div>
      </Sequence>

      {/* Tech Stack - Frontend - 540 to 720 frames */}
      <Sequence from={540} durationInFrames={180}>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", height: "100%", padding: 80 }}>
          <h2 style={{ fontSize: 72, color: "#10B981", marginBottom: 60, opacity: spring({ frame: frame - 540, fps, config: { damping: 200, stiffness: 100 } }) }}>
            Frontend Tech Stack
          </h2>
          <div style={{ display: "flex", gap: 24, flexWrap: "wrap", justifyContent: "center", maxWidth: 1400 }}>
            <TechBadge name="Vue 3" color="#42B883" frame={frame} delay={555} />
            <TechBadge name="TypeScript" color="#3178C6" frame={frame} delay={565} />
            <TechBadge name="Element Plus" color="#409EFF" frame={frame} delay={575} />
            <TechBadge name="Vite" color="#646CFF" frame={frame} delay={585} />
            <TechBadge name="Pinia" color="#FFD700" frame={frame} delay={595} />
            <TechBadge name="RSocket Client" color="#F59E0B" frame={frame} delay={605} />
          </div>
        </div>
      </Sequence>

      {/* Architecture - 720 to 900 frames */}
      <Sequence from={720} durationInFrames={180}>
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", height: "100%", padding: 60 }}>
          <h2 style={{ fontSize: 72, color: "#F59E0B", marginBottom: 16, opacity: spring({ frame: frame - 720, fps, config: { damping: 200, stiffness: 100 } }) }}>
            Architecture Patterns
          </h2>
          <p style={{ fontSize: 32, color: "#D1D5DB", marginBottom: 30, opacity: spring({ frame: frame - 730, fps, config: { damping: 200, stiffness: 80 } }) }}>
            Domain-Driven Design with Strict Layer Separation
          </p>
          <ArchitectureDiagram frame={frame} />
          <div style={{ marginTop: 30, display: "flex", gap: 40, opacity: spring({ frame: frame - 750, fps, config: { damping: 200, stiffness: 80 } }) }}>
            <div style={{ fontSize: 24, color: "#9CA3AF" }}>🔄 Reactive Throughout</div>
            <div style={{ fontSize: 24, color: "#9CA3AF" }}>🎯 Aggregate Pattern</div>
            <div style={{ fontSize: 24, color: "#9CA3AF" }}>💾 Dual Persistence</div>
          </div>
        </div>
      </Sequence>

      {/* Frontend Showcase - 900 to 1080 frames */}
      <Sequence from={900} durationInFrames={180}>
        <FrontendShowcase frame={frame} />
      </Sequence>
    </AbsoluteFill>
  );
};
