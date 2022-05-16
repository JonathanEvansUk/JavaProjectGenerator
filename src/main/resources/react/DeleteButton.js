import { Button } from "react-bootstrap";

const DeleteButton = ({ id, handleDeleteShow }) => {
  const onClick = () => handleDeleteShow(id);
  return (
    <Button
      className="float-right"
      size="sm"
      variant="danger"
      onClick={onClick}
    >
      Delete
    </Button>
  );
};

export default DeleteButton;
