import Form from "@rjsf/bootstrap-4";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function ViewUser() {
  const { id } = useParams();

  const [formData, setFormData] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/user/${id}`)
    .then(response => response.json())
    .then(data => setFormData(data));
  },[]);

  const schema = {
  "title" : "View User",
  "description" : "Description",
  "type" : "object",
  "properties" : {
    "id" : {
      "type" : [ "string" ]
    }
  },
  "required" : [ "id" ]
};

  const uiSchema = {
   "ui:submitButtonOptions": {
      "norender": true,
    }
  };

  return (<Form schema={schema} formData={formData} uiSchema={uiSchema} readonly />);
};