import Form from "@rjsf/bootstrap-4";
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { Button } from "react-bootstrap";
import { replaceNullsWithUndefined } from "../../utils/Utils.js";

export default function EditBill() {
  const [formData, setFormData] = useState(null);
  const { id } = useParams();


  useEffect(() => {
    const fetchData = async () => {
      const [currentBill] = await Promise.all([
          fetchExisting(id)
      ]);

      setFormData(currentBill);
    };

    fetchData();
  }, []);

  const submitForm = ({ formData }) => {
    fetch("http://localhost:8080/bill/" + id, {
      method: "PUT",
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
  return (<Form schema={schema} formData={formData} onSubmit={submitForm} uiSchema={uiSchema}  />);
};

const fetchExisting = (id) => {
  return fetch(`http://localhost:8080/bill/${id}`)
      .then(response => response.json())
      .then(data => replaceNullsWithUndefined(data))
;
};
