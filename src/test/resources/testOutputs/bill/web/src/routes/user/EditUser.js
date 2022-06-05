import Form from "@rjsf/bootstrap-4";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function EditUser() {
  const [formData, setFormData] = useState(null);
  const { id } = useParams();

  useEffect(() => {
    fetch(`http://localhost:8080/user/${id}`)
    .then(response => response.json())
    .then(data => setFormData(data));
  },[]);

  const submitForm = ({ formData }) => {
    fetch("http://localhost:8080/user", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(formData)
    }).then(response => console.log(response));
  };

  const schema = {
  "title" : "Edit User",
  "description" : "Description",
  "type" : "object",
  "properties" : {
    "id" : {
      "type" : [ "string" ]
    }
  },
  "required" : [ "id" ]
};
  return (<Form schema={schema} formData={formData} onSubmit={submitForm} />);
};