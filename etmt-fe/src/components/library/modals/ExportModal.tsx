import { FilePdfOutlined, FileTextOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { Button, Form, Input, Modal, notification } from "antd";
import { FunctionComponent, useState } from "react";
import { apiExportPdf, apiExportXml } from "../../../common/APIUtils";
import { ERR_MSG, SUCCESS_MSG } from "../../../common/Common";
import { useAuth } from "../../auth/Auth";
import { ItemModalProps } from "../items/ItemInfo";

const ExportModal: FunctionComponent<ItemModalProps> = (props: ItemModalProps) => {
  const auth = useAuth();
  const [exportXmlLoading, setXmlExportLoading] = useState(false);
  const [exportPdfLoading, setPdfExportLoading] = useState(false);
  const [downloadUrl, setDownloadUrl] = useState<string | null>(null)

  const exportXML = (formValues: { parentName: string }) => {
    console.log("export xml: ", props.selectedItemData?.title);
    setXmlExportLoading(true);
    apiExportXml(props.selectedItemData?.id, formValues.parentName, auth.user?.token)
      .then((response: Blob) => {
        notification.success({
          message: SUCCESS_MSG,
          description: 'Variant exported to XML!'
        });
        console.log(response);
        const url = URL.createObjectURL(response);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", `ETMT_${formValues.parentName}.xml`);
        link.click();
        setDownloadUrl(url);
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while exporting the test variant... Please try again.'
        })
      }).finally(() => setXmlExportLoading(false))
  }

  const exportPDF = () => {
    console.log("export pdf: ", props.selectedItemData?.title);
    setPdfExportLoading(true);
    apiExportPdf(props.selectedItemData?.id, auth.user?.token)
      .then((response: Blob) => {
        notification.success({
          message: SUCCESS_MSG,
          description: 'Variant exported to PDF!'
        });
        console.log(response);
        const url = URL.createObjectURL(response);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", `ETMT_${props.selectedItemData?.title}.pdf`);
        link.click();
        setDownloadUrl(url);
      }).catch(error => {
        console.error(error);
        notification.error({
          message: ERR_MSG,
          description: 'There was an error while exporting the test variant... Please try again.'
        })
      }).finally(() => setPdfExportLoading(false))
  }

  const handleXmlExport = (formValues: { parentName: string }) => {
    exportXML(formValues);
  }

  const handleClose = () => {
    setDownloadUrl(null);
    props.modal.setModalState({ isVisible: false, isEdit: false });
    props.onModalClose();
  }

  const handleCancel = () => {
    props.modal.setModalState({ isVisible: false, isEdit: false });
  }

  const handleSubmitFailed = (error: any) => {
    console.error(error);
  }

  return (
    <Modal
      title={"Export test variant: " + props.selectedItemData?.title}
      visible={props.modal.modalState.isVisible}
      confirmLoading={exportXmlLoading}
      onCancel={handleCancel}
      footer={[
        <Button type="primary" form="exportXmlForm" key="submit" htmlType="submit" loading={exportXmlLoading} icon={<FileTextOutlined />}>
          Export XML
        </Button>,
        <Button type="primary" loading={exportPdfLoading} onClick={exportPDF} icon={<FilePdfOutlined />}>
          Export PDF
        </Button>
      ]}
    >
      <p className="infoMsg"><InfoCircleOutlined /> Export to XML requires a parent category name specified. After import in moodle, all questions imported will be placed under this category and will be tagged with its name</p>
      <Form
        name="exportXmlForm"
        labelCol={{ span: 10 }}
        layout="horizontal"
        onFinish={handleXmlExport}
        onFinishFailed={handleSubmitFailed}
      >
        <Form.Item
          label="Moodle parent category"
          name="parentName"
          rules={[{ required: true, message: 'Parent category name cannot be empty!' }]}
        >
          <Input />
        </Form.Item>

      </Form>
      {downloadUrl && <a id="downloadLink" download="" href={downloadUrl} onClick={handleClose} hidden>Download</a>}
    </Modal>
  );
}

export default ExportModal;
