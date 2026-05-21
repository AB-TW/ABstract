import { Composition } from "remotion";
import { ABtractIntro } from "./ABtractIntro";

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
    </>
  );
};
