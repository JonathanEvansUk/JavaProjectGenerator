import { useEffect, useState } from "react";
import Table from "react-bootstrap/Table";
import Card from "react-bootstrap/Card";
import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { Link } from "react-router-dom";
import DeleteModal from "../../utils/DeleteModal";
import DeleteButton from "../../utils/DeleteButton";

export default function BillList() {
  let [billList, setBillList] = useState([]);

  const [showDelete, setShowDelete] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const hideDelete = () => setShowDelete(false);
  const handleDeleteShow = deleteId => {
    setShowDelete(true);
    setDeleteId(deleteId);
  };

  useEffect(() => {
    fetch("http://localhost:8080/bill")
    .then(response => response.json())
    .then(data => {
      setBillList(data);
    });
  },[]);

  const tableHeaders = [
      "id",
      "amount",
      "dateReceived",
      "paid",
      "datePaid",
      "paymentType"
  ];

  console.log(billList);
  const rows = billList.map(bill => createRow(bill, tableHeaders, handleDeleteShow));

  const performDelete = deleteId => {
    console.log("Deleting: ", deleteId);
    fetch(`http://localhost:8080/bill/${deleteId}`, {
      method: "DELETE"
    })
      .then(() => setBillList(billList.filter(bill => bill.id !== deleteId)))
      .then(() => setDeleteId(null))
      .then(() => hideDelete());
  };

  return (
    <Card>
      <Card.Header className="d-flex justify-content-between align-items-center">
        <h4 className="m-0 font-weight-normal">Bill</h4>
        <Button as={Link} to="/entities/bill/create">
          Create a Bill
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
          title="Delete Bill"
          body={`Are you sure you want to delete Bill: ${deleteId}`}
          hide={hideDelete}
          action={() => performDelete(deleteId)}
      />
    </Card>);
};

const createRow = (bill, tableHeaders, handleDeleteShow) => {
  return (
    <tr key={ bill.id }>
        <td key="id" className="align-middle" style={ {"lineHeight": "1em"} }>
            { bill["id"] }
        </td>
        <td key="amount" className="align-middle" style={ {"lineHeight": "1em"} }>
            { bill["amount"] }
        </td>
        <td key="dateReceived" className="align-middle" style={ {"lineHeight": "1em"} }>
            { bill["dateReceived"] }
        </td>
        <td key="paid" className="align-middle" style={ {"lineHeight": "1em"} }>
            <i className={ bill["paid"] ? "bi bi-check-lg text-success" : ""} style={ { "fontSize": "2em", "lineHeight": "1em"} }></i>
        </td>
        <td key="datePaid" className="align-middle" style={ {"lineHeight": "1em"} }>
          { bill["datePaid"] && new Date(bill["datePaid"]).toLocaleString()}
        </td>
        <td key="paymentType" className="align-middle" style={ {"lineHeight": "1em"} }>
            { bill["paymentType"] }
        </td>
      <td>
        <div className="float-right">
          <Button as={Link} to={"/entities/bill/" + bill.id} variant="info" size="sm">
            <i className="bi bi-eye-fill pr-1"></i>
            View</Button>
          <Button className="mx-sm-1" as={Link} to={"/entities/bill/" + bill.id + "/edit"} variant="primary" size="sm">
            <i className="bi bi-pencil-fill pr-1"></i>
            Edit</Button>
          <DeleteButton id={ bill.id } handleDeleteShow={handleDeleteShow} />
        </div>
      </td>
    </tr>
  );
};