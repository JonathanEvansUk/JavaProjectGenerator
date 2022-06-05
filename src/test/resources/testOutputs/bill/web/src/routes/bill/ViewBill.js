import Form from "@rjsf/bootstrap-4";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function ViewBill() {
  const { id } = useParams();

  const [formData, setFormData] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/bill/${id}`)
    .then(response => response.json())
    .then(data => setFormData(data));
  },[]);

  const schema = {
  "title" : "View Bill",
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

  const uiSchema = {
   "ui:submitButtonOptions": {
      "norender": true,
    }
  };

  return (<Form schema={schema} formData={formData} uiSchema={uiSchema} readonly  disabled/>);
};