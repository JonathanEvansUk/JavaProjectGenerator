import Form from "@rjsf/bootstrap-4";
import { useNavigate, Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { Button } from "react-bootstrap";

export default function CreateBill() {
  const navigate = useNavigate();
  const submitForm = ({ formData }) => {
    console.log(JSON.stringify(formData));


    fetch("http://localhost:8080/bill", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(formData)
    })
      .then(response => {
        if (!response.ok) {
          throw Error(response.statusText);
        }
        return response;
      })
      .then(response => console.log(response))
      .then(() => navigate("/entities/Bill"))
      .catch(error => console.log(error));
  };

  const schema = {
  "title" : "Create a Bill",
  "description" : "Description",
  "type" : "object",
  "properties" : {
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
      "enum" : [ ],
      "type" : [ "string" ]
    }
  },
  "required" : [ "paymentType" ]
};
  const uiSchema = {
  };

  return (<Form schema={schema} onSubmit={submitForm} uiSchema={uiSchema}  />);
};
