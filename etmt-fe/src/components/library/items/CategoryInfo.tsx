import { FunctionComponent } from "react";
import { Category } from "../../../common/Common";
import { getIconForItemType } from "./ItemInfo";

interface CategoryInfoProps {
  category: Category
}

const CategoryInfo: FunctionComponent<CategoryInfoProps> = (props: CategoryInfoProps) => {

  return (
    <div>
      <h3>{getIconForItemType(props.category?.type)} {props.category?.title}</h3>
    </div>
  );
}

export default CategoryInfo;
