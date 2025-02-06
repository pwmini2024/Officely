import '../pages/Styles.css';

function SearchBar(props) {

  return (
      <div className='searchBar-container'>
          <input
              className='searchBar'
              type="text"
              placeholder="Search..."
              value={props.searchTerm}
              onChange={props.onChange}
          />
          <button className='button-searchBar' onClick={props.onSearch}
          >Search</button>
      </div>
  );
}
  
export default SearchBar;