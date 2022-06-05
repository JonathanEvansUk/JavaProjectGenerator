import Form from "@rjsf/bootstrap-4";
import { useNavigate } from "react-router-dom";

export default function CreateUser() {
  const navigate = useNavigate();

  const submitForm = ({ formData }) => {
    console.log(JSON.stringify(formData));

    fetch("http://localhost:8080/user", {
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
      .then(() => navigate("/viewUser"))
      .catch(error => console.log(error));
  };

  const schema = {
  "title" : "Create a User",
  "description" : "Description",
  "type" : "object",
  "properties" : {
    "id" : {
      "type" : [ "string" ]
    }
  },
  "required" : [ "id" ]
};
  return (<Form schema={schema} onSubmit={submitForm} />);
};