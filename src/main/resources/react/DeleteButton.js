import { Button } from "react-bootstrap";

const DeleteButton = ({ id, handleDeleteShow }) => {
  const onClick = () => handleDeleteShow(id);
  return (
    <Button
      size="sm"
      variant="danger"
      onClick={onClick}
    >
      <i className="bi bi-trash3-fill pr-1"></i>
      Delete
    </Button>
  );
};

export default DeleteButton;
