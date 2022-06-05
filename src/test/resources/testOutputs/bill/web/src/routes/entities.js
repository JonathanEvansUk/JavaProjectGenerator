import { Card, Table } from "react-bootstrap";
import { Link } from "react-router-dom";

export default function Entities() {
return (
<div>
  <Card>
    <Table>
      <thead>
      <tr>
        <th>Entities</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>
          <Link to="/entities/bill">Bill</Link>
        </td>
      </tr>
      </tbody>
    </Table>
  </Card>
</div>
);
}
