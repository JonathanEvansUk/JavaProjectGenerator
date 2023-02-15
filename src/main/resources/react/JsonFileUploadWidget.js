import { JsonViewer } from "@textea/json-viewer";
import { Button, Form } from "react-bootstrap";
import { JSON_VIEWER_THEME } from "./JsonViewerTheme";

const JsonFileUploadWidget = (props) => {
  const handleChange = (event) => {
    const reader = new FileReader();
    reader.onload = event => props.onChange(event.target.result);
    reader.filename = event.target.files[0].name;
    reader.readAsText(event.target.files[0]);
  };

  const value = props.value && JSON.parse(props.value);

  // Hack to fix bug where JsonViewer initially loads with original theme, before reloading with custom theme.
  // Flashes quickly but doesn't look good.
  const style = {
    "display": value ? "" : "none",
    "width": "100%",
  };

  return (<>
    <Form.Label className={props.rawErrors?.length > 0 ? "text-danger" : ""}>
      {props.label || props.schema.title}
      {(props.label || props.schema.title) && props.required ? "*" : null}
    </Form.Label>
    {!props.readonly && <Button size="sm" as={Form.Label} className="float-right"
            htmlFor={"fileUpload_" + props.id}>Upload File</Button>}
    <div className="form-control-file">
      <input type="file" id={"fileUpload_" + props.id} required={props.required}
             style={{ display: "none" }}
             onChange={handleChange}/>
      <JsonViewer editable={false}
                  style={style}
                  value={value}
                  rootName={false}
                  displayDataTypes={false}
                  collapseStringsAfterLength={false}
                  theme={JSON_VIEWER_THEME}/>
    </div>
  </>);
};

export default JsonFileUploadWidget;
