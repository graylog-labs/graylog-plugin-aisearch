import React, { useState } from 'react';

const SamplePage = () => {
  const [response, setResponse] = useState(''); // AI's response
  const [loading, setLoading] = useState(false); // Loading state
  const [error, setError] = useState(null); // Error state

  // Function to handle API request
  const fetchAIResponse = async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch('http://localhost:9000/api/plugins/org.graylog.aisearch/aisearch/fetch-logs', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch AI logs from the backend');
      }

      const data = await response.json(); // Assuming JSON response
      setResponse(data); // Update the state with the backend response
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };



  return (
    <div style={{ padding: '20px' }}>
      <h1>AI-Powered Graylog Insights</h1>
      <p>Click the button below to get AI-generated insights.</p>

      <div style={{ marginBottom: '10px' }}>
        <button onClick={fetchAIResponse} disabled={loading}>
          {loading ? 'Loading...' : 'Fetch AI Response'}
        </button>
      </div>

      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {response && (
        <div style={{ marginTop: '20px', padding: '10px', border: '1px solid #ddd', backgroundColor: '#f9f9f9' }}>
          <h3>AI's Response:</h3>
          <p>{response}</p>
        </div>
      )}
    </div>
  );
};

export default SamplePage;
