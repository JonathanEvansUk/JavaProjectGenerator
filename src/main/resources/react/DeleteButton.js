import { Button } from "react-bootstrap";

const DeleteButton = ({ id, entityName, handleDeleteShow }) => {
  const onClick = () => handleDeleteShow(id);
  return (
    <Button
      className="float-right"
      size="sm"
      variant="danger"
      onClick={onClick}
    >
      Delete {entityName}
    </Button>
  );
};

export default DeleteButton;
