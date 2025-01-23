import React from 'react';
import { Row, Col } from 'react-bootstrap';
import { PageHeader } from 'components/common';

function SamplePage() {
  return (
    <PageHeader title="Sample Plugin">
      <span>
        Hello from the Sample plugin!
      </span>
    </PageHeader>
  );
}

export default SamplePage;