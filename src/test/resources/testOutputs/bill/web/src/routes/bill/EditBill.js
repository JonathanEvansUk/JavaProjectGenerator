import Form from "@rjsf/bootstrap-4";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function EditBill() {
  const [formData, setFormData] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    fetch(`http://localhost:8080/bill/${id}`)
    .then(response => response.json())
    .then(data => setFormData(data));
  },[]);

  const submitForm = ({ formData }) => {
    fetch("http://localhost:8080/bill", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(formData)
    }).then(response => console.log(response));
  };

  const schema = {
  "title" : "Edit Bill",
  "description" : "Description",
  "type" : "object",
  "properties" : {
    "id" : {
      "type" : [ "integer" ]
    },
    "amount" : {
      "type" : [ "number", "null" ]
    },
    "dateReceived" : {
      "type" : [ "string", "null" ],
      "format" : "date"
    },
    "paid" : {
      "type" : [ "boolean", "null" ]
    },
    "datePaid" : {
      "type" : [ "string", "null" ],
      "format" : "date-time"
    },
    "paymentType" : {
      "enum" : [ "Credit", "Debit" ],
      "type" : [ "string" ]
    }
  },
  "required" : [ "id", "paymentType" ]
};
  return (<Form schema={schema} formData={formData} onSubmit={submitForm} />);
};