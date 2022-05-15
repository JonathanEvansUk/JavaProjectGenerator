import { Modal } from "react-bootstrap";
import { Button } from "react-bootstrap";

const DeleteModal = ({ show, title, body, hide, action }) => {
  return (
    <Modal show={show}>
      <Modal.Header>
        <Modal.Title>{title}</Modal.Title>
      </Modal.Header>
      <Modal.Body>{body}</Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={hide}>
          Cancel
        </Button>
        <Button variant="danger" onClick={action}>
          Delete
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default DeleteModal;
