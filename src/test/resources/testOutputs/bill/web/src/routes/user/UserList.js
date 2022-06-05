import { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { Link } from "react-router-dom";
import DeleteModal from "../../utils/DeleteModal";
import DeleteButton from "../../utils/DeleteButton";

export default function UserList() {
  let [userList, setUserList] = useState([]);

  const [showDelete, setShowDelete] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const hideDelete = () => setShowDelete(false);
  const handleDeleteShow = deleteId => {
    setShowDelete(true);
    setDeleteId(deleteId);
  };

  useEffect(() => {
    fetch("http://localhost:8080/user")
    .then(response => response.json())
    .then(data => {
      setUserList(data);
    });
  },[]);

  const tableHeaders = [
      "id"
  ];

  console.log(userList);
  const rows = userList.map(user => createRow(user, tableHeaders, handleDeleteShow));

  const performDelete = deleteId => {
    console.log("Deleting: ", deleteId);
    fetch(`http://localhost:8080/user/${deleteId}`, {
      method: "DELETE"
    })
      .then(() => setUserList(userList.filter(user => user.id !== deleteId)))
      .then(() => setDeleteId(null))
      .then(() => hideDelete());
  };

  return (
    <Card>
      <Card.Header className="d-flex justify-content-between align-items-center">
        <h4 className="m-0 font-weight-normal">User</h4>
        <Button as={Link} to="/user">
          Create a User
        </Button>
      </Card.Header>
      <Table responsive>
        <thead>
          <tr>
            {tableHeaders.map(header => <th key={header}>{header}</th>)}
          </tr>
        </thead>

        <tbody>
          {rows}
        </tbody>
      </Table>

      <DeleteModal
          show={showDelete}
          title="Delete User"
          body={`Are you sure you want to delete User: ${deleteId}`}
          hide={hideDelete}
          action={() => performDelete(deleteId)}
      />
    </Card>);
};

const createRow = (user, tableHeaders, handleDeleteShow) => {
  return (
    <tr key={ user.id }>
        <td key="{ id }" className="align-middle" style={ {"lineHeight": "1em"} }>
          
          { user["id"] }
        </td>
      <td>
        <div className="float-right">
          <Button as={Link} to={"/user/" + user.id} variant="info" size="sm">
            <i className="bi bi-eye-fill pr-1"></i>
            View</Button>
          <Button className="mx-sm-1" as={Link} to={"/user/" + user.id + "/edit"} variant="primary" size="sm">
            <i className="bi bi-pencil-fill pr-1"></i>
            Edit</Button>
          <DeleteButton id={ user.id } handleDeleteShow={handleDeleteShow} />
        </div>
      </td>
    </tr>
  );
};