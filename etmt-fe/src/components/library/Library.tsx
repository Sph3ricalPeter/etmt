import { FolderOutlined, ReloadOutlined } from "@ant-design/icons";
import { Button, Col, Divider, Layout, Row, Typography } from "antd";
import { Content } from "antd/lib/layout/layout";
import Tree from "antd/lib/tree";
import { FunctionComponent, useEffect, useState } from "react";
import { apiGetLibrary, apiMoveItem } from "../../common/APIUtils";
import { Item, ItemType } from "../../common/Common";
import { useAuth } from "../auth/Auth";
import ItemInfo, { getIconForItemType } from "./items/ItemInfo";

const { Title } = Typography;

interface LibraryProps {

}

const Library: FunctionComponent<LibraryProps> = () => {
  const auth = useAuth();
  const [isLibraryLoading, setIsLibraryLoading] = useState(false);
  const [treeData, setTreeData] = useState<any[]>([]);
  const [expandedKeys, setExpandedKeys] = useState<Array<string>>([]);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);

  // fetch library on load
  useEffect(() => {
    fetchLibrary();
  }, [auth.user.token]); // [] execute on user token value change, could be replaced with just [] which is basicly onLoad ..

  const compare = (a: Item, b: Item) => {
    let aIndex = Object.keys(ItemType).indexOf(a.type);
    let bIndex = Object.keys(ItemType).indexOf(b.type);
    if (aIndex < bIndex) {
      return -1;
    }
    if (aIndex > bIndex) {
      return 1;
    }
    return 0;
  }

  const fetchLibrary = () => {
    setIsLibraryLoading(true);
    setTimeout(
      () => {
        apiGetLibrary(auth.user.token)
          .then(response => {
            console.log("fetched library: ", response);
            var data = response;
            setIconsAndSortRecursive(data[0]);
            setTreeData(data);
            selectedItem ? setSelectedItem(findItemByIdIter(selectedItem?.id, data[0])) : setSelectedItem(null);
          }).catch(error => {
            console.error("error fetching library: ", error);
          }).finally(() => setIsLibraryLoading(false))
      },
      500
    );
  }

  const setIconsAndSortRecursive = (item: any) => {
    item.icon = getIconForItemType(item?.type as ItemType);
    // item.children?.sort(compare);
    item.children?.forEach((element: any) => {
      setIconsAndSortRecursive(element);
    });
  }

  const findItemByIdIter = (id: string, root: any) => {
    var stack = [], item, ii;
    stack.push(root);

    while (stack.length > 0) {
      item = stack.pop();
      if (item.id === id) {
        return item;
      } else if (item.children && item.children.length) {
        for (ii = 0; ii < item.children.length; ii += 1) {
          stack.push(item.children[ii]);
        }
      }
    }

    // Didn't find it. Return null.
    return null;
  }

  const onSelect = (keys: any, info: any) => {
    console.log("onSelect: ", keys, info);

    setSelectedItem(info.node);
  };

  const onDrop = (info: any) => {
    // TODO: clean this up a bit
    // console.log("item dropped: ", info);

    const dropKey = info.node.key;
    const dragKey = info.dragNode.key;
    const dropPos = dropKey.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

    var actualPosition = dropPosition;

    console.log(`dropkey: ${dropKey}, dragKey: ${dragKey}, dropPos: ${dropPos}, dropPosition: ${dropPosition}`);


    const loop = (data: any, key: any, callback: any) => {
      for (let i = 0; i < data.length; i++) {
        if (data[i].key === key) {
          return callback(data[i], i, data);
        }
        if (data[i].children) {
          loop(data[i].children, key, callback);
        }
      }
    };
    const data = [...treeData];

    // Find dragObject
    let draggedItem: any;
    loop(data, dragKey, (item: any, index: any, arr: any) => {
      arr.splice(index, 1);
      draggedItem = item;
    });

    let newParentId: any;
    if (!info.dropToGap) {
      console.log("item dropped onto another item, move to first position");

      // item dropped onto another item
      loop(data, dropKey, (newParent: any) => {
        newParentId = newParent.id;
        newParent.children = newParent.children || [];
        // insert at the beginning of that element's children
        newParent.children.unshift(draggedItem);
      });
    } else if (
      (info.node.props.children || []).length > 0 && // Has children
      info.node.props.expanded && // Is expanded
      dropPosition === 1 // On the bottom gap
    ) {
      console.log("item dropped into gap, different parent");

      loop(data, dropKey, (newParent: any) => {
        newParentId = newParent.id;
        newParent.children = newParent.children || [];
        // where to insert 示例添加到头部，可以是随意位置
        newParent.children.unshift(draggedItem);
        // in previous version, we use item.children.push(dragObj) to insert the
        // item to the tail of the children
      });
    } else {
      console.log("item dropped into gap, same parent");

      let ar: any;
      let i: any;
      loop(data, dropKey, (item: any, index: any, arr: any) => {
        newParentId = item.parentId;
        ar = arr;
        i = index;
      });
      if (dropPosition === -1) {
        actualPosition = i;
        ar.splice(i, 0, draggedItem);
      } else {
        actualPosition = i + 1;
        ar.splice(i + 1, 0, draggedItem);
      }
    }

    console.log(`move item ${draggedItem.title} to position ${actualPosition} under new parent ${newParentId}`);

    apiMoveItem(draggedItem, newParentId, actualPosition, auth.user?.token)
      .then(() => {
        console.log("ITEM MOVED");

      }).catch(error => {
        console.error(error);
      }).finally(() => fetchLibrary());

    // setTreeData(data);
  };

  const onExpand = (expandedKeys: Array<any>, info: any) => {
    console.log("expanded key:", info);
    setExpandedKeys(expandedKeys);
  }

  const library = [
    {
      title: 'Root Category',
      key: '0-0',
      icon: <FolderOutlined />,
      children: [
        {
          title: 'Subcategory',
          key: '0-0-0',
          icon: <FolderOutlined />,
          children: [],
        }
      ]
    }
  ]

  return (
    <Layout className="site-layout-background">
      <Content className="site-layout-background"
        style={{
          padding: 24,
          margin: 0,
          minHeight: "90vh",
        }}>
        <Title level={3} style={{ paddingLeft: "25px", paddingBottom: "15px" }}>Library</Title>

        <Divider orientation="left" orientationMargin="25px">
          Browser
          <Button loading={isLibraryLoading} onClick={fetchLibrary} style={{ margin: "0 15px" }} icon={<ReloadOutlined />}>
            Refresh
          </Button>
        </Divider>

        <Row wrap={false}>
          <Col flex="600px">
            <Tree
              defaultExpandAll
              showIcon={true}
              selectedKeys={[selectedItem?.key]}
              expandedKeys={expandedKeys}
              defaultExpandedKeys={expandedKeys}
              onSelect={onSelect}
              onExpand={onExpand}
              onDrop={onDrop}
              treeData={treeData}
              draggable={true}
              style={{ padding: "15px" }}
            />
          </Col>
          <Col flex={3} >
            {
              selectedItem !== null ?
                <div style={{ padding: "15px" }}>
                  <ItemInfo selectedItem={selectedItem} selectedItemParent={findItemByIdIter(selectedItem.parentId, treeData[0])} onContentUpdate={fetchLibrary} />
                </div> :
                <></>
            }
          </Col>
        </Row>
      </Content>
    </Layout>
  );
}

export default Library;
