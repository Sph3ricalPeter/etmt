import { Table } from "antd";
import { FunctionComponent } from "react";
import { ItemType, Template, Topic } from "../../../common/Common";
import { getIconForItemType } from "./ItemInfo";

interface TemplateInfoProps {
  template: Template
}

const TemplateInfo: FunctionComponent<TemplateInfoProps> = (props: TemplateInfoProps) => {
  return (
    <div>
      <h3>{getIconForItemType(ItemType.template)} {props.template?.title}</h3>
      <Table columns={[
        {
          title: 'Topic',
          dataIndex: 'topic',
          key: 'topic'
        },
        {
          title: 'Categories',
          dataIndex: 'categories',
          key: 'categories',
        },
        {
          title: 'Points',
          dataIndex: 'points',
          key: 'points',
        },
        {
          title: 'Question count',
          dataIndex: 'questionCount',
          key: 'questionCount',
        }
      ]} dataSource={props.template.topics?.map((item: Topic) => {
        var n = props.template.topics?.indexOf(item) + 1;
        var categories = item.categories?.flatMap(row => row[row.length - 1].title).join(", ");
        console.log(categories);
        return {
          key: n,
          topic: "Topic " + n,
          categories: categories,
          points: item.points,
          questionCount: item.questionCount
        }

      }) as Array<any>}
        scroll={{ y: 300 }}
        pagination={{ hideOnSinglePage: true }}
      />
    </div>
  );
}

export default TemplateInfo;
