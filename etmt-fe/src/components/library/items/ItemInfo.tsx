import { DeleteOutlined, EditOutlined, ExportOutlined, FileOutlined, FileTextOutlined, FileUnknownOutlined, FolderOutlined, ImportOutlined, QuestionCircleOutlined } from "@ant-design/icons"
import { Affix, Button, Card, Divider, Skeleton, Space, Tooltip } from "antd"
import { FunctionComponent, useEffect, useState } from "react"
import { apiGetItem } from "../../../common/APIUtils"
import { Category, Item, ItemType, Question, Template, Variant } from "../../../common/Common"
import { useAuth } from "../../auth/Auth"
import CategoryModal from "../modals/CategoryModal"
import ExportModal from "../modals/ExportModal"
import GenerateModal from "../modals/GenerateModal"
import ImportModal from "../modals/ImportModal"
import QuestionModal from "../modals/QuestionModal"
import RemoveItemModal from "../modals/RemoveItemModal"
import TemplateModal from "../modals/TemplateModal"
import VariantModal from "../modals/VariantModal"
import CategoryInfo from "./CategoryInfo"
import QuestionInfo from "./QuestionInfo"
import TemplateInfo from "./TemplateInfo"
import VariantInfo from "./VariantInfo"

export const getIconForItemType = (type: ItemType) => {
  switch (type) {
    case ItemType.question:
      return <QuestionCircleOutlined />
    case ItemType.template:
      return <FileOutlined />
    case ItemType.category:
      return <FolderOutlined />
    case ItemType.variant:
      return <FileTextOutlined />
    default:
      return <FileUnknownOutlined />
  }
}

export interface ModalState {
  isVisible: boolean,
  isEdit: boolean
}

const MODAL_INIT: ModalState = {
  isVisible: false,
  isEdit: false
}

export interface ModalStateInterface {
  modalState: ModalState,
  setModalState: React.Dispatch<React.SetStateAction<ModalState>>;
}

export interface ItemModalProps {
  modal: ModalStateInterface,
  selectedItemData: any,
  onModalClose: () => void
}

export interface ItemInfoProps {
  selectedItem: Item,
  selectedItemParent: Item,
  onContentUpdate: () => void
}

const ItemInfo: FunctionComponent<ItemInfoProps> = (props: ItemInfoProps) => {
  const auth = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [fetchedItem, setFetchedItem] = useState<any | null>(null);
  const [fetchedParent, setFetchedParent] = useState<any | null>(null);

  const [questionModalState, setQuestionModalState] = useState<ModalState>(MODAL_INIT);
  const [categoryModalState, setCategoryModalState] = useState<ModalState>(MODAL_INIT);
  const [templateModalState, setTemplateModalState] = useState<ModalState>(MODAL_INIT);
  const [variantModalState, setVariantModalState] = useState<ModalState>(MODAL_INIT);
  const [removeItemModalState, setRemoveItemModalState] = useState<ModalState>(MODAL_INIT);
  const [exportModalState, setExportModalState] = useState<ModalState>(MODAL_INIT);
  const [xmlImportModalState, setXmlImportModalState] = useState<ModalState>(MODAL_INIT);
  const [generateModalState, setGenerateModalState] = useState<ModalState>(MODAL_INIT);

  const [content, setContent] = useState<any>(null);

  // on selectedItem state update
  useEffect(() => {
    if (props.selectedItem === null) {
      return;
    }

    onSelectedItemChanged();
  }, [props.selectedItem]);

  // set content after fetched item changed
  useEffect(() => {
    if (fetchedItem == null) {
      return;
    }

    switch (fetchedItem.type) {
      case ItemType.category:
        setContent(<CategoryInfo category={fetchedItem as Category} />);
        break;
      case ItemType.question:
        setContent(<QuestionInfo question={fetchedItem as Question} />);
        break;
      case ItemType.template:
        setContent(<TemplateInfo template={fetchedItem as Template} />);
        break;
      case ItemType.variant:
        setContent(<VariantInfo variant={fetchedItem as Variant} />);
        break;
      default:
        throw new Error(`item type ${props.selectedItem.type} is not supported!`);
    }
  }, [fetchedItem])

  const getActionsFormItemType = (itemType: ItemType) => {
    switch (itemType) {
      case ItemType.question:
        return [
          <Tooltip title="Edit question">
            <Button key="editQuestion" onClick={() => setQuestionModalState({ isVisible: true, isEdit: true })} icon={<EditOutlined />}>Edit</Button>
          </Tooltip>,
          <Tooltip title="Delete question">
            <Button key="deleteQuestion" onClick={() => setRemoveItemModalState({ isVisible: true, isEdit: false })} icon={<DeleteOutlined />} danger>Delete</Button>
          </Tooltip>,
        ]
      case ItemType.template:
        return [
          <Tooltip title="Generate test variants">
            <Button type="primary" key="generate" onClick={() => setGenerateModalState({ isVisible: true, isEdit: false })} icon={<FileTextOutlined />}>Generate</Button>
          </Tooltip>,
          <Tooltip title="Edit template">
            <Button key="editTemplate" onClick={() => setTemplateModalState({ isVisible: true, isEdit: true })} icon={<EditOutlined />}>Edit</Button>
          </Tooltip>,
          <Tooltip title="Delete template">
            <Button key="deleteTemplate" onClick={() => setRemoveItemModalState({ isVisible: true, isEdit: false })} icon={<DeleteOutlined />} danger>Delete</Button>
          </Tooltip>,
        ]
      case ItemType.category:
        return [
          <Tooltip title="Add subcategory">
            <Button key="addSubcategory" onClick={() => setCategoryModalState({ isVisible: true, isEdit: false })} icon={<FolderOutlined />}>Add subcategory</Button>
          </Tooltip>,
          <Tooltip title="Add question">
            <Button key="addQuestion" onClick={() => setQuestionModalState({ isVisible: true, isEdit: false })} icon={<QuestionCircleOutlined />}>Add question</Button>
          </Tooltip>,
          <Tooltip title="Add test template">
            <Button key="addTemplate" onClick={() => setTemplateModalState({ isVisible: true, isEdit: false })} icon={<FileOutlined />}>Add template</Button>
          </Tooltip>,
          <Tooltip title="Import category">
            <Button key="importCategory" onClick={() => setXmlImportModalState({ isVisible: true, isEdit: false })} icon={<ImportOutlined />}>Import</Button>
          </Tooltip>,
          <Tooltip title="Edit category">
            <Button key="editCategory" onClick={() => setCategoryModalState({ isVisible: true, isEdit: true })} icon={<EditOutlined />}>Edit</Button>
          </Tooltip>,
          <Tooltip title="Delete category">
            <Button key="deleteCategory" onClick={() => setRemoveItemModalState({ isVisible: true, isEdit: false })} icon={<DeleteOutlined />} danger>Delete</Button>
          </Tooltip>
        ]
      case ItemType.variant:
        return [
          <Tooltip title="Export test variant">
            <Button type="primary" key="exportVariant" onClick={() => setExportModalState({ isVisible: true, isEdit: false })} icon={<ExportOutlined />}>Export</Button>
          </Tooltip>,
          <Tooltip title="Edit test variant">
            <Button key="editVariant" onClick={() => setVariantModalState({ isVisible: true, isEdit: true })} icon={<EditOutlined />}>Edit</Button>
          </Tooltip>,
          <Tooltip title="Delete test variant">
            <Button key="deleteVariant" onClick={() => setRemoveItemModalState({ isVisible: true, isEdit: false })} icon={<DeleteOutlined />} danger>Delete</Button>
          </Tooltip>
        ]
      default:
        throw new Error(`actions for item type ${itemType} is not supported`);
    }
  }

  const onSelectedItemChanged = () => {
    if (props.selectedItem === null) {
      return;
    }
    setIsLoading(true);
    Promise.all([
      apiGetItem(props.selectedItem.id, props.selectedItem.type, auth.user.token)
        .then(response => {
          console.log("set fetched item: ", response);
          setFetchedItem(response);
        }).catch(error => {
          console.error("error while fetching: ", error);
        }),
      props.selectedItem.parentId ? apiGetItem(props.selectedItem.parentId, ItemType.category, auth.user?.token)
        .then(response => {
          console.log("set fetched parent: ", response);
          setFetchedParent(response);
        }).catch(error => {
          console.error("error while fetching: ", error);
        }) : Promise.resolve()
    ]).finally(() => setIsLoading(false));
  }

  return (
    <Affix offsetTop={75}>
      <Card style={{ width: "100%" }}>
        <Skeleton loading={isLoading} style={{ width: "100%" }}>
          {content}
          <Divider />
          <Space wrap={true}>
            {getActionsFormItemType(props.selectedItem.type)}
          </Space>
        </Skeleton>

        <QuestionModal
          modal={
            {
              modalState: questionModalState,
              setModalState: setQuestionModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <CategoryModal
          modal={
            {
              modalState: categoryModalState,
              setModalState: setCategoryModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <TemplateModal
          modal={
            {
              modalState: templateModalState,
              setModalState: setTemplateModalState
            }
          }
          selectedItemData={fetchedItem}
          parentItemData={fetchedParent}
          onModalClose={props.onContentUpdate}
        />

        <VariantModal
          modal={
            {
              modalState: variantModalState,
              setModalState: setVariantModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <GenerateModal
          modal={
            {
              modalState: generateModalState,
              setModalState: setGenerateModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <ExportModal
          modal={
            {
              modalState: exportModalState,
              setModalState: setExportModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <ImportModal
          modal={
            {
              modalState: xmlImportModalState,
              setModalState: setXmlImportModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

        <RemoveItemModal
          modal={
            {
              modalState: removeItemModalState,
              setModalState: setRemoveItemModalState
            }
          }
          selectedItemData={fetchedItem}
          onModalClose={props.onContentUpdate}
        />

      </Card>
    </Affix>
  );
}

export default ItemInfo;
