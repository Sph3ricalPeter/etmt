import { CheckCircleTwoTone, StopTwoTone } from "@ant-design/icons";
import { Divider, List } from "antd";
import { FunctionComponent } from "react";
import { ItemType, Question } from "../../../common/Common";
import { getIconForItemType } from "./ItemInfo";

interface QuestionInfoProps {
  question: Question
}

const QuestionInfo: FunctionComponent<QuestionInfoProps> = (props: QuestionInfoProps) => {
  return (
    <div>
      <h3>{getIconForItemType(ItemType.question)} {props.question?.title}</h3>
      <div style={{ padding: "5px" }}>
        <p>
          type: <i>{props.question?.questionType}</i>
        </p>
        <p dangerouslySetInnerHTML={{ __html: props.question?.text }}>
        </p>
        <i>
          {props.question?.points} points, {props.question?.penalty} penalty
        </i>
      </div>

      <Divider orientation="left" orientationMargin={0}>Answers</Divider>
      <List
        size="small"
        dataSource={props.question?.answers}
        renderItem={
          item =>
            <List.Item>
              {item.isCorrect ?
                <CheckCircleTwoTone twoToneColor="#52c41a" /> :
                <StopTwoTone twoToneColor="#eb2f96" />} {item.text}
            </List.Item>}
      />
    </div>
  );
}

export default QuestionInfo;
