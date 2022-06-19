import { WarningOutlined } from "@ant-design/icons";
import { Divider, Table } from "antd";
import { FunctionComponent } from "react";
import { ItemType, Question, Variant } from "../../../common/Common";
import { getIconForItemType } from "./ItemInfo";

interface VariantInfoProps {
  variant: Variant
}

const VariantInfo: FunctionComponent<VariantInfoProps> = (props: VariantInfoProps) => {

  const formatDate = (date: any) => {
    let d = new Date(date);
    return (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear() + " " +
      d.getHours() + ":" + d.getMinutes();
  }

  return (
    <div className="itemInfo">
      <h3>{getIconForItemType(ItemType.variant)} {props.variant?.title}</h3>
      {props.variant?.date ?
        <>
          <Divider orientation="left" orientationMargin="0px">Date</Divider>
          <p>{formatDate(props.variant?.date)}</p>
        </> :
        <>
          <p className="warnMsg"><WarningOutlined /> Missing test date</p>
        </>
      }
      {props.variant?.instructions ?
        <>
          <Divider orientation="left" orientationMargin="0px">Instructions</Divider>
          <p>{props.variant?.instructions}</p>
        </> :
        <>
          <p className="warnMsg"><WarningOutlined /> Missing test instructions</p>
        </>
      }

      <Divider orientation="left" orientationMargin="0px">Questions</Divider>
      <Table columns={[
        {
          title: 'Number',
          dataIndex: 'number',
          key: 'number'
        },
        {
          title: 'Title',
          dataIndex: 'title',
          key: 'title'
        },
        {
          title: 'Points',
          dataIndex: 'points',
          key: 'points',
        }
      ]} dataSource={props.variant?.questions?.map((item: Question) => {
        var n = props.variant?.questions?.indexOf(item) + 1;
        return {
          key: n,
          number: n,
          title: item.title,
          points: item.points
        }
      }) as Array<any>}
        scroll={{ y: "30vh" }}
        pagination={false}
      />
    </div>
  );
}

export default VariantInfo;
