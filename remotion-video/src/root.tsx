import { Composition, Folder } from "remotion";
import { ABtractIntro } from "./ABtractIntro";
import { SYSTEM_INTRO_SCENES, SYSTEM_INTRO_TOTAL_FRAMES, SystemIntroduction } from "./system-intro/SystemIntroduction";

export const RemotionRoot: React.FC = () => {
  return (
    <>
      <Composition
        id="ABtractIntro"
        component={ABtractIntro}
        durationInFrames={1080}
        fps={30}
        width={1920}
        height={1080}
        defaultProps={{
          theme: "dark"
        }}
      />
      <Folder name="SystemIntroduction-Scenes">
        {SYSTEM_INTRO_SCENES.map((scene) => (
          <Composition
            key={scene.id}
            id={scene.id}
            component={scene.component}
            durationInFrames={scene.durationInFrames}
            fps={30}
            width={1920}
            height={1080}
          />
        ))}
      </Folder>
      <Composition
        id="SystemIntroduction"
        component={SystemIntroduction}
        durationInFrames={SYSTEM_INTRO_TOTAL_FRAMES}
        fps={30}
        width={1920}
        height={1080}
      />
    </>
  );
};
