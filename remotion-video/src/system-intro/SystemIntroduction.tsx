import React from "react";
import { AbsoluteFill, Audio, interpolate, staticFile, useVideoConfig } from "remotion";
import { TransitionSeries, linearTiming } from "@remotion/transitions";
import { fade } from "@remotion/transitions/fade";
import { TitleScene } from "./TitleScene";
import { FeatureScene } from "./FeatureScene";
import { ConfigScene } from "./ConfigScene";
import { ExperimentScene } from "./ExperimentScene";
import { ArchitectureScene } from "./ArchitectureScene";
import { RSocketScene } from "./RSocketScene";
import { OutroScene } from "./OutroScene";

export const SYSTEM_INTRO_SCENES = [
  { id: "SystemIntroTitle", component: TitleScene, durationInFrames: 150 },
  { id: "SystemIntroFeature", component: FeatureScene, durationInFrames: 210 },
  { id: "SystemIntroConfig", component: ConfigScene, durationInFrames: 210 },
  { id: "SystemIntroExperiment", component: ExperimentScene, durationInFrames: 240 },
  { id: "SystemIntroArchitecture", component: ArchitectureScene, durationInFrames: 240 },
  { id: "SystemIntroRSocket", component: RSocketScene, durationInFrames: 210 },
  { id: "SystemIntroOutro", component: OutroScene, durationInFrames: 150 },
];

const TRANSITION_FRAMES = 14;

export const SYSTEM_INTRO_TOTAL_FRAMES =
  SYSTEM_INTRO_SCENES.reduce((sum, s) => sum + s.durationInFrames, 0) -
  TRANSITION_FRAMES * (SYSTEM_INTRO_SCENES.length - 1);

const BackgroundMusic: React.FC = () => {
  const { durationInFrames } = useVideoConfig();
  return (
    <Audio
      src={staticFile("audio/mixkit-tech-house-vibes-130.mp3")}
      volume={(f) =>
        interpolate(f, [0, 20, durationInFrames - 45, durationInFrames], [0, 0.3, 0.3, 0], {
          extrapolateLeft: "clamp",
          extrapolateRight: "clamp",
        })
      }
    />
  );
};

export const SystemIntroduction: React.FC = () => {
  return (
    <AbsoluteFill style={{ backgroundColor: "#070d1a" }}>
      <TransitionSeries>
        {SYSTEM_INTRO_SCENES.map((scene, i) => (
          <React.Fragment key={scene.id}>
            <TransitionSeries.Sequence durationInFrames={scene.durationInFrames} name={scene.id}>
              <scene.component />
            </TransitionSeries.Sequence>
            {i < SYSTEM_INTRO_SCENES.length - 1 && (
              <TransitionSeries.Transition
                presentation={fade()}
                timing={linearTiming({ durationInFrames: TRANSITION_FRAMES })}
              />
            )}
          </React.Fragment>
        ))}
      </TransitionSeries>
      <BackgroundMusic />
    </AbsoluteFill>
  );
};
