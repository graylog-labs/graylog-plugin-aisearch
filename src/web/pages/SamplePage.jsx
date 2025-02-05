import React, { useState } from 'react';

const SamplePage = () => {
  const [response, setResponse] = useState(''); // AI's response
  const [loading, setLoading] = useState(false); // Loading state
  const [error, setError] = useState(null); // Error state

  const fetchAIResponse = async () => {
    setLoading(true);
    setError(null);

    try {
      const res = await fetch(
        'http://localhost:9000/api/plugins/org.graylog.aisearch/aisearch/fetch-logs',
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );

      if (!res.ok) {
        const errorDetails = await res.json();
        throw new Error(errorDetails.message || 'Failed to fetch AI response');
      }

      const data = await res.json();
      const aiContent =
        data?.choices?.[0]?.message?.content || 'No response content available.';
      setResponse(aiContent);
    } catch (err) {
      setError(err.message || 'An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>AI-Powered Graylog Insights</h1>
      <p>Click the button below to get AI-generated insights.</p>

      <div style={{ marginBottom: '10px' }}>
        <button
          onClick={fetchAIResponse}
          disabled={loading}
          style={{
            backgroundColor: '#0073e6',
            color: 'white',
            border: 'none',
            padding: '10px 20px',
            borderRadius: '5px',
            cursor: 'pointer',
            opacity: loading ? 0.6 : 1,
          }}
        >
          {loading ? 'Loading...' : 'Fetch AI Response'}
        </button>
      </div>

      {loading && (
        <div style={{ marginTop: '10px', color: '#0073e6' }}>
          Fetching AI response, please wait...
        </div>
      )}

      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          <strong>Error:</strong> {error}
        </div>
      )}

      {response && (
        <div
          style={{
            marginTop: '20px',
            padding: '10px',
            border: '1px solid #ddd',
            borderRadius: '5px',
            whiteSpace: 'pre-wrap',
            wordWrap: 'break-word',
          }}
        >
          <h3>AI's Response:</h3>
          <p>{response}</p>
        </div>
      )}

      {!response && !error && !loading && (
        <div style={{ marginTop: '10px', color: '#888' }}>
          No AI response yet. Click the button to fetch insights.
        </div>
      )}
    </div>
  );
};

export default SamplePage;
